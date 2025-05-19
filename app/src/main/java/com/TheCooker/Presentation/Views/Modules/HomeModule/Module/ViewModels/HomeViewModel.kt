package com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.Common.Layer.Resources.uploadDownloadResourceWithPagination
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.SharedModule.CommonActionLikePost
import com.TheCooker.dataLayer.Repositories.RecipeRepo
import com.TheCooker.dataLayer.Repositories.UserRepo
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val recipeRepo: RecipeRepo,
    private val userRepo: UserRepo,
    private val userDataProvider: UserDataProvider
): CommonActionLikePost() {


    init{
        viewModelScope.launch {
            fetchRandomSharesForHomeView()
        }
    }

    override val _openCommentsPostId = MutableStateFlow<String?>(null)

    override fun setOpenCommentsPostId(postId: String?) {
        _openCommentsPostId.value = postId
    }
    override fun getOpenCommentsPostId(): String? {
        return _openCommentsPostId.value
    }

    private val _randomShares = MutableStateFlow<List<UserMealDetailModel>>(emptyList())
    val randomShares: StateFlow<List<UserMealDetailModel>> get() = _randomShares

    private val _errorFetchingShares = MutableStateFlow<String?>(null)
    val errorFetchingShares: StateFlow<String?> get() = _errorFetchingShares

    private val likeCommentNumberListeners = mutableMapOf<String, ListenerRegistration>()

    private val commentListeners = mutableMapOf<String, ListenerRegistration>()

    private val _commentsLikes = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val commentsLikes: StateFlow<Map<String, Boolean>> get() = _commentsLikes

    override val commentTobeDeletedOrUpdated = mutableStateOf<PostCommentModel?>(null)


    override fun setCommentToBeDeletedOrUpdated(comment: PostCommentModel?){
        commentTobeDeletedOrUpdated.value = comment
    }
    override fun getCommentToBeDeletedOrUpdated(): PostCommentModel?{
        return commentTobeDeletedOrUpdated.value
    }




    @Override
    override  fun resetLikedUsers() {
        _likedUsers.value = emptyList()
        paginationIndex = 0
    }

    override fun updateLikes(posts: List<UserMealDetailModel>) {
        val userId = userDataProvider.userData?.uid ?: return
        val likesMap = posts.associate { post ->
            post.recipeId to (post.whoLikeIt?.contains(userId) == true)
        }
        if (likesMap.isNotEmpty()) {
            _postLikes.value = likesMap
        }
    }


    override fun togglePostLike(share: UserMealDetailModel) {
        userDataProvider.userData?.uid ?: return
        val postId = share.recipeId ?: return
        val hasLiked = _postLikes.value[postId] == true

        Log.d("togglePostLike", "hasLiked: $hasLiked")

        viewModelScope.launch {
            val success = if (hasLiked) {
                unLikePost(share)
            } else {
                postLike(share)
            }

            if (success) {
                toggleLikeForPostSupport(postId, !hasLiked)
            } else {
                Log.d("togglePostLike", "Failed to toggle like for postId: $postId")
            }
        }
    }



    @Override
    override fun toggleLikeForPostSupport(postId: String, liked: Boolean) {
        val updatedLikes = _postLikes.value.toMutableMap()
        updatedLikes[postId] = liked
        _postLikes.value = updatedLikes
    }

    @Override
    override fun toggleLikeForComment(commentId: String, liked: Boolean) {
        // Ενημερώνουμε την κατάσταση liked για το συγκεκριμένο σχόλιο
        val updatedLikes = _commentsLikes.value.toMutableMap()
        updatedLikes[commentId] = liked
        _commentsLikes.value = updatedLikes
    }





     private fun startListeningToCommentCount(recipeId: String) {
        if (likeCommentNumberListeners.containsKey(recipeId)) return // Υπάρχει ήδη

        val listener = recipeRepo.listenToCommentCount(recipeId) { newCount ->
            val updatedShares = _randomShares.value.map { share ->
                if (share.recipeId == recipeId) {
                    share.copy(countComments = newCount)
                } else share
            }
            _randomShares.value = updatedShares
        }

        likeCommentNumberListeners[recipeId] = listener
    }

    @Override
    override  fun startListeningToPostLikes(recipeId: String) {
        if (likePostNumberListeners.containsKey(recipeId)) return // Υπάρχει ήδη
        val listener = recipeRepo.listenToPostLikesCount(recipeId){
            val updatedShares = _randomShares.value.map { share ->
                if (share.recipeId == recipeId) {
                    share.copy(countLikes = it)
                } else share
            }
            _randomShares.value = updatedShares
        }
        likePostNumberListeners[recipeId] = listener
    }

    override suspend fun updateComment(comment: PostCommentModel) {
        try {
            if(_updateComment.value.isNullOrEmpty()){ return }
            comment.comment = _updateComment.value.toString()
            when (val response = recipeRepo.updateComment(comment)) {
                is uploadDownloadResource.Success -> {
                    Log.d("ProfileViewModel", "Comment updated successfully")
                }

                is uploadDownloadResource.Error -> {
                    Log.d("ProfileViewModel", "Error updating comment: ${response.exception}")
                }
            }
        }catch (e: Exception){
            Log.d("ProfileViewModel", "Error updating comment: ${e.message}")
        }
    }

    override fun checkIfIsUserComment(): Boolean{
        return commentTobeDeletedOrUpdated.value?.senderObj?.uid == userDataProvider.userData?.uid
    }

    private fun startListeningToComments(recipeId: String, onChange: (List<PostCommentModel>) -> Unit) {
        if(commentListeners.containsKey(recipeId)) return
        val listener = recipeRepo.listenToCommentsForPost(recipeId, onChange)

        commentListeners[recipeId] = listener
    }

    override fun onCleared() {
        super.onCleared()
        commentListeners.forEach { (_, listener) -> listener.remove() }
        commentListeners.clear()

        likeCommentNumberListeners.forEach { (_, listener) -> listener.remove() }
        likeCommentNumberListeners.clear()

        likePostNumberListeners.forEach { (_, listener) -> listener.remove() }
        likePostNumberListeners.clear()
    }

    @Override
    override suspend fun postLike(share: UserMealDetailModel): Boolean{
        when(val response = recipeRepo.likePost(share, userDataProvider.userData?.uid ?: "")){
            is uploadDownloadResource.Success -> {
                Log.d("HomeViewModel", "Post liked successfully")
                return true
            }

            is uploadDownloadResource.Error -> {
                Log.d("HomeViewModel", "Error liking post: ${response.exception}")
                return false
            }
        }
    }




    @Override
    override suspend fun commentLike(comment: PostCommentModel): Boolean{
        when(val response = recipeRepo.commentLike(comment, userDataProvider.userData?.uid ?: "")){
            is uploadDownloadResource.Success -> {
                Log.d("HomeViewModel", "Comment liked successfully")
                return true
            }
            is uploadDownloadResource.Error -> {
                Log.d("HomeViewModel", "Error liking comment: ${response.exception}")

            }
        }
        return false
    }

    @Override
    override suspend fun unLikePost(share: UserMealDetailModel): Boolean {
        when (val response = recipeRepo.unLikePost(share, userDataProvider.userData?.uid ?: "")) {
            is uploadDownloadResource.Success -> {
                Log.d("HomeViewModel", "Post unliked successfully")
                return true
            }
            is uploadDownloadResource.Error -> {
                Log.d("HomeViewModel", "Error unliking post: ${response.exception}")
                return false
            }
        }
    }

    @Override
    override suspend fun unLikeComment(comment: PostCommentModel): Boolean {
        when (val response = recipeRepo.unLikeComment(comment)) {
            is uploadDownloadResource.Success -> {
                Log.d("HomeViewModel", "Comment unliked successfully")
                return true
            }

            is uploadDownloadResource.Error -> {
                Log.d("HomeViewModel", "Error unliking comment: ${response.exception}")
                return false
            }
        }
    }

    @Override
    override suspend fun deleteComment(comment: String) {
        when (val response = recipeRepo.deleteComment(comment)) {
            is uploadDownloadResource.Success -> {
                Log.d("HomeViewModel", "Comment deleted successfully")

            }
            is uploadDownloadResource.Error -> {
                Log.d("HomeViewModel", "Error deleting comment: ${response.exception}")

            }
        }
    }



    private var paginationIndex = 0

    override suspend fun loadUsersWithPagination(comment: PostCommentModel?, share: UserMealDetailModel?) {
        if (_loadingState.value) {
            Log.d("Pagination", "Already loading, skipping request")
            return // μην φορτώνεις ξανά αν είναι ήδη σε loading
        }

        _loadingState.value = true
        Log.d("Pagination", "Starting to load users with pagination. Current startIndex: $paginationIndex")

        val response = if(comment?.commentId?.isNotEmpty() == true){ recipeRepo.fetchUsersWhoLikedSpecificCommentWithPagination(
            comment = comment,
            startIndex = paginationIndex,
            pageSize = 10
        )}else{ recipeRepo.fetchLikesForPostWithPagination(
            share = share,
            startIndex = paginationIndex,
            pageSize = 10
        )}

        when (response) {
            is uploadDownloadResourceWithPagination.Success -> {
                val newUsers = response.data
                Log.d("Pagination", "Fetched ${newUsers.size} new users.")

                if (newUsers.isNotEmpty()) {
                    // Ενημερώνουμε τη λίστα των χρηστών
                    _likedUsers.value += newUsers
                    // Ενημερώνουμε το paginationIndex
                    paginationIndex = response.lastVisible ?: paginationIndex
                    Log.d("Pagination", "Updated pagination index to: $paginationIndex")
                } else {
                    Log.d("Pagination", "No new users fetched.")
                }

                _loadingState.value = false
                Log.d("Pagination", "Loading completed.")
            }

            is uploadDownloadResourceWithPagination.Error -> {
                Log.d("Pagination", "Error: ${response.exception}")
                _loadingState.value = false
            }
        }
    }



    suspend fun fetchRandomSharesForHomeView() {
        Log.d("HomeViewModel", "Recompose")

        when (val response = recipeRepo.fetchRandomSharesForHomeView()) {
            is uploadDownloadResource.Success<List<UserMealDetailModel>> -> {
                val newFetchedRecipes = response.data
                updateLikes(newFetchedRecipes)


                Log.d("HomeViewModel", "Fetched recipes count: ${newFetchedRecipes.size}")
                Log.d("HomeViewModel", "Fetched recipe IDs: ${newFetchedRecipes.map { it.recipeId }}")

                val currentShares = _randomShares.value
                Log.d("HomeViewModel", "Current _randomShares size: ${currentShares.size}")
                Log.d("HomeViewModel", "Current _randomShares recipe IDs: ${currentShares.map { it.recipeId }}")

                val currentRecipeIds = currentShares.mapNotNull { it.recipeId }.toSet()

                // Φιλτράρουμε τα νέα για να βρούμε μόνο αυτά που δεν υπάρχουν ήδη
                val newOnlyRecipes = newFetchedRecipes.filter { it.recipeId !in currentRecipeIds }

                Log.d("HomeViewModel", "Newly detected recipes (not in _randomShares): ${newOnlyRecipes.size}")
                Log.d("HomeViewModel", "NewOnlyRecipe IDs: ${newOnlyRecipes.map { it.recipeId }}")

                val recipesToUse = if (newOnlyRecipes.isNotEmpty()) {
                    Log.d("HomeViewModel", "New recipes found: ${newOnlyRecipes.size}")
                    newOnlyRecipes + _randomShares.value
                } else {
                    Log.d("HomeViewModel", "No new recipes, shuffling existing ones.")
                    _randomShares.value.shuffled().also {
                        Log.d("HomeViewModel", "Shuffled result size: ${it.size}")
                    }
                }

                val commentsResponse = recipeRepo.fetchComments(recipesToUse)

                val recipesWithCreators = mutableListOf<UserMealDetailModel>()
                val initialLikedComments = mutableMapOf<String, Boolean>()

                for (recipe in recipesToUse) {
                    val creatorId = recipe.creatorId ?: continue
                    val creator = userRepo.getUserById(creatorId)

                    if (creator is LoginResults.Success) {
                        val relatedComments = if (commentsResponse is uploadDownloadResource.Success) {
                            commentsResponse.data
                                .filter { it.postId == recipe.recipeId }
                                .sortedByDescending { it.timestamp }
                        } else emptyList()

                        val updatedComments = relatedComments.map { comment ->
                            val isLiked = comment.whoLikeIt?.contains(userDataProvider.userData?.uid) == true
                            initialLikedComments[comment.commentId] = isLiked
                            comment.copy(isLiked = isLiked)
                        }

                        val enrichedRecipe = recipe.copy(
                            creatorData = creator.data,
                            comments = updatedComments
                        )

                        if (creator.data.uid != userDataProvider.userData?.uid) {
                            recipesWithCreators.add(enrichedRecipe)
                        }

                        enrichedRecipe.recipeId?.let { it ->
                            startListeningToCommentCount(it)
                            startListeningToPostLikes(it)
                            startListeningToComments(it) { newComments ->
                                val userId = userDataProvider.userData?.uid
                                val enrichedComments = newComments.map { comment ->
                                    comment.copy(isLiked = comment.whoLikeIt?.contains(userId) == true)
                                }

                                val currentSharesLive = _randomShares.value.toMutableList()
                                val index = currentSharesLive.indexOfFirst { it.recipeId == recipe.recipeId }

                                if (index != -1) {
                                    val updatedShare = currentSharesLive[index].copy(comments = enrichedComments)
                                    currentSharesLive[index] = updatedShare
                                    _randomShares.value = currentSharesLive
                                }
                            }
                        }
                    }
                }

                _commentsLikes.value = initialLikedComments

                val finalList = recipesWithCreators.shuffled()

                _openCommentsPostId.value = null


                Log.d("HomeViewModel", "Final list assigned to _randomShares. Size: ${finalList.size}")
                _randomShares.value = finalList.map { it.copy() }
            }

            is uploadDownloadResource.Error -> {
                _errorFetchingShares.value = "Something went wrong. Please try again."
                Log.d("HomeViewModel", "Error fetching shares: ${response.exception}")
            }
        }
    }
}


