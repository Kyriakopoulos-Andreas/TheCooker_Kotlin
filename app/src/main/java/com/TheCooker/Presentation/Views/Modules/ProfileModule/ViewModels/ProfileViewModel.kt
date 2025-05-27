package com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.Common.Layer.Resources.uploadDownloadResourceWithPagination
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.SharedModule.CommonActionLikePost
import com.TheCooker.dataLayer.Repositories.RecipeRepo
import com.TheCooker.dataLayer.Repositories.UserRepo
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val _userRepo: UserRepo,
    private val _userDataProvider: UserDataProvider,
    private val _recipeRepo: RecipeRepo
) : CommonActionLikePost() {




    val userDataProvider: State<UserDataProvider> get() = mutableStateOf(_userDataProvider)

    private val _information = mutableStateOf(false)
    val information: State<Boolean> get() = _information

    private val _editProfile = mutableStateOf(false)
    val editProfile: State<Boolean> get() = _editProfile

    private val _showShares= mutableStateOf(false)
    val showShares: State<Boolean> get() = _showShares

    private val _country by lazy { mutableStateOf("") }
    val country: State<String> get() = _country

    private val _city= mutableStateOf("")
    val city: State<String> get() = _city

    private val _chefLevelList = mutableListOf("Beginner", "Intermediate", "Advanced", "Expert")
    val chefLevelList: List<String> get() = _chefLevelList

    private val _chefLevel = mutableStateOf("")
    val chefLevel: State<String> get() = _chefLevel

    private val _specialties = mutableStateOf("")
    val specialties: State<String> get() = _specialties

    private val _errorFetchingShares = mutableStateOf("")
    val errorFetchingShares: State<String> get() = _errorFetchingShares

    private val _deletePostResult = mutableStateOf("")
    val deletePostResult: State<String> get() = _deletePostResult

    private val _shares = MutableStateFlow<List<UserMealDetailModel>>(emptyList())
    val shares: MutableStateFlow<List<UserMealDetailModel>> get() = _shares

    private val _goldenChefHats = mutableIntStateOf(0)
    val goldenChefHats: State<Int> get() = _goldenChefHats
    private val _saveInfoResult = mutableStateOf<String?>(null)
    val saveInfoResult: State<String?> get() = _saveInfoResult

    private val _postCommentMessage = mutableStateOf<String?>("Write Comment")
    val postCommentMessage: State<String?> get() = _postCommentMessage

    private var _postComment = mutableStateOf<PostCommentModel?>(null)
    val postComment: State<PostCommentModel?> get() = _postComment

    private val _postCommentResult = mutableStateOf<String?>(null)
    val postCommentResult: State<String?> get() = _postCommentResult

    private var _commentButtonState = mutableStateOf(false)
    val commentButtonState: State<Boolean> get() = _commentButtonState

    private var _shareCommentsCount = mutableIntStateOf(0)
    val shareCommentsCount: State<Int> get() = _shareCommentsCount

    private val commentListeners = mutableMapOf<String, ListenerRegistration>()

    private val commentLikeListeners = mutableMapOf<String, ListenerRegistration>()

    private val _likedComments = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val likedComments: StateFlow<Map<String, Boolean>> get() = _likedComments

    private val _user = MutableStateFlow<UserDataModel?>(null)
    val user: StateFlow<UserDataModel?> get() = _user

    private val _isFriend = MutableStateFlow<Boolean>(false)
    val isFriend: StateFlow<Boolean> get() = _isFriend

    private val _owner = MutableStateFlow<Boolean>(false)
    val owner: StateFlow<Boolean> get() = _owner


    private val _friendshipState = MutableStateFlow(FriendshipStatus.FRIENDS) // Ξεκινά ως φίλοι
    val friendshipState: StateFlow<FriendshipStatus> = _friendshipState

    enum class FriendshipStatus {
        FRIENDS,
        NOT_FRIENDS,
        PENDING
    }


    fun setProfileOwner(owner: Boolean){
        _owner.value = owner
    }

    init {
        viewModelScope.launch {
            user
                .filterNotNull()
                .distinctUntilChanged()
                .collect { nonNullUser ->
                    fetchShares(nonNullUser)
                }
        }
    }

    fun setUserData(user: UserDataModel){
        _user.value = user
    }



    fun setLoggedInProfile(){
        _user.value = _userDataProvider.userData
    }



    fun setUserInfo(user: UserDataModel){

        _country.value = user.country.toString().takeIf {
            it.isNotBlank()
        } ?: ""
        _city.value = user.city.toString().takeIf {
            it.isNotBlank()
        }.toString()

        _chefLevel.value = user.chefLevel.toString().takeIf {
            it.isNotBlank()
        }.toString()

        _specialties.value = user.specialties.toString().takeIf {
            it.isNotBlank()
        }.toString()

        val goldenChefHatsValue = user.goldenChefHats

        if (goldenChefHatsValue != null && goldenChefHatsValue > 0) {
            _goldenChefHats.intValue = goldenChefHatsValue
        }


    }

    override val commentTobeDeletedOrUpdated = mutableStateOf<PostCommentModel?>(null)


    override fun setCommentToBeDeletedOrUpdated(comment: PostCommentModel?){
        commentTobeDeletedOrUpdated.value = comment
    }
    override fun getCommentToBeDeletedOrUpdated(): PostCommentModel?{
        return commentTobeDeletedOrUpdated.value
    }


    override fun updateLikes(posts: List<UserMealDetailModel>) {
        val userId = userDataProvider.value.userData?.uid ?: return
        val likesMap = posts.associate { post ->
            post.recipeId to (post.whoLikeIt?.contains(userId) == true)
        }
        if (likesMap.isNotEmpty()) {
            _postLikes.value = likesMap
        }
    }

    @Override
    override fun togglePostLike(share: UserMealDetailModel) {
        userDataProvider.value.userData?.uid ?: return
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

    override fun checkIfIsUserComment(): Boolean{
        if(commentTobeDeletedOrUpdated.value?.senderObj?.uid == userDataProvider.value.userData?.uid){
            return true
        }else{
            return false
        }
    }

    override suspend fun updateComment(comment: PostCommentModel) {
        try {
            if(_updateComment.value.isNullOrEmpty()){ return }
            comment.comment = _updateComment.value.toString()
            when (val response = _recipeRepo.updateComment(comment)) {
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








    private fun startListeningToCommentCount(recipeId: String) {
        if (commentListeners.containsKey(recipeId)) return // Ήδη υπάρχει

        val listener = _recipeRepo.listenToCommentCount(recipeId) { newCount ->
            val updatedShares = _shares.value.map { share ->
                if (share.recipeId == recipeId) {
                    share.copy(countComments = newCount)
                } else share
            }
            _shares.value = updatedShares
        }

        commentListeners[recipeId] = listener
    }

    fun startListeningToCommentLikes(recipeId: String) {
        if (commentLikeListeners.containsKey(recipeId)) return

        val listener = _recipeRepo.listenToCommentsForPost(recipeId) { newComments ->
            val currentUserId = _userDataProvider.userData?.uid
            val updatedLikes = mutableMapOf<String, Boolean>()

            newComments.forEach { comment ->
                val isLiked = comment.whoLikeIt?.contains(currentUserId) == true
                updatedLikes[comment.commentId] = isLiked
            }

            _likedComments.value = updatedLikes

            // Αν θες να ενημερώσεις και τα shares για εμφάνιση στο UI
            _shares.value = _shares.value.map { share ->
                if (share.recipeId == recipeId) {
                    share.copy(comments = newComments)
                } else share
            }
        }

        commentLikeListeners[recipeId] = listener
    }

    @Override
    override  fun resetLikedUsers() {
        _likedUsers.value = emptyList()
        paginationIndex = 0
    }
    override val _openCommentsPostId = MutableStateFlow<String?>(null)

    override fun setOpenCommentsPostId(postId: String?) {
        _openCommentsPostId.value = postId
    }
    override fun getOpenCommentsPostId(): String? {
        return _openCommentsPostId.value
    }



    override fun onCleared() {
        super.onCleared()
        commentLikeListeners.forEach { (_, listener) -> listener.remove() }
        commentLikeListeners.clear()

        commentListeners.forEach { (_, listener) -> listener.remove() }
        commentListeners.clear()
    }

    fun onPostCommentMessageChange(comment: String) {
        _postCommentMessage.value = comment
    }

    @Override
    override suspend fun commentLike(comment: PostCommentModel): Boolean{
        when(val response = _recipeRepo.commentLike(comment, _userDataProvider.userData?.uid ?: "")){ //!!!!!!!!!!!!
            is uploadDownloadResource.Success -> {
                Log.d("ProfileViewModel", "Comment liked successfully")
                return true
            }
            is uploadDownloadResource.Error -> {
                Log.d("ProfileViewModel", "Error liking comment: ${response.exception}")

            }
        }
        return false
    }

    @Override
    override suspend fun postLike(share: UserMealDetailModel): Boolean{
        when(val response = _recipeRepo.likePost(share, userDataProvider.value.userData?.uid ?: "") ){
            is uploadDownloadResource.Success -> {
                if(!owner.value){
                    try {
                        _userRepo.sendPostLikeNotification(
                            sender = userDataProvider.value.userData!!,
                            postId = share.recipeId ?: "",
                            receiver = _user.value ?: return false

                        )
                    }catch (e: Exception){
                        Log.d("HomeViewModel", "Error sending post like notification: ${e.message}")
                    }
                }
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
    override  fun startListeningPostLikesCount(recipeId: String) {
        if (likesCountPostListeners.containsKey(recipeId)) return // Υπάρχει ήδη
        val listener = _recipeRepo.listenToPostLikesCount(recipeId){
            val updatedShares = _shares.value.map { share ->
                if (share.recipeId == recipeId) {
                    share.copy(countLikes = it)
                } else share
            }
            _shares.value = updatedShares
        }
        likesCountPostListeners[recipeId] = listener


    }

    override fun startListeningPostLikes(recipeId: String, onChange: (List<String>) -> Unit) {
        if(likesPostListeners.containsKey(recipeId)) return
        val listener = _recipeRepo.listenPostLikes(recipeId, onChange)
        likesPostListeners[recipeId] = listener
    }
    @Override
    override fun updateButtonStateForPostLikes(postId: String, isLoading: Boolean) {
        val currentList = _shares.value.toMutableList()
        val index = currentList.indexOfFirst { it.recipeId == postId }

        if (index != -1) {
            val oldItem = currentList[index]
            currentList[index] = oldItem.copy(isLikeButtonLoading = isLoading)
            _shares.value = currentList
        }
    }
    override fun updateCommentLikeLoadingState(commentId: String, isLoading: Boolean) {
        val updatedShares = _shares.value.map { share ->
            val updatedComments = share.comments?.map { comment ->
                if (comment.commentId == commentId) {
                    comment.copy(isLikeButtonLoading = isLoading)
                } else comment
            }
            share.copy(comments = updatedComments)
        }

        _shares.value = updatedShares
    }

    @Override
    override suspend fun unLikePost(share: UserMealDetailModel): Boolean {
        when (val response = _recipeRepo.unLikePost(share, userDataProvider.value.userData?.uid ?: "")) {
            is uploadDownloadResource.Success -> {
                if(!owner.value){
                    try {
                        _userRepo.deleteLikeNotification(
                            sender = userDataProvider.value.userData!!,
                            postId = share.recipeId ?: "",
                            receiver = _user.value ?: return false

                        )
                    }catch (e: Exception){
                        Log.d("HomeViewModel", "Error sending post like notification: ${e.message}")
                    }
                }
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
        when (val response = _recipeRepo.unLikeComment(comment)) {
            is uploadDownloadResource.Success -> {

                Log.d("ProfileViewModel", "Comment unliked successfully")
                return true
            }

            is uploadDownloadResource.Error -> {
                Log.d("ProfileViewModel", "Error unliking comment: ${response.exception}")
                return false
            }
        }
    }



    suspend fun createPostComment(postId: String){
        try{
            if(_postCommentMessage.value == "Write Comment" || _postCommentMessage.value == ""){
                _postCommentMessage.value = null

            }
            _commentButtonState.value = true

            Log.d("ProfileViewModel", _postCommentMessage.value.toString())
            if(!_postCommentMessage.value.isNullOrEmpty() && _postCommentMessage.value!= null){
                Log.d("ProfileViewModel", "test")
                Log.d("ProfileViewModel", "Creating post comment")
                Log.d("ProfileViewModel", "Current comment: ${_postComment.value}")

                val comment = PostCommentModel(
                    postId = postId,
                    comment = _postCommentMessage.value ?: "",
                    commentId = UUID.randomUUID().toString(),
                    senderId = _userDataProvider.userData?.uid ?: "",
                    timestamp = System.currentTimeMillis(),
                    countLikes = 0,
                    senderObj = _userDataProvider.userData


                )

                val result = _recipeRepo.createPostComment(comment)

                Log.d("ProfileViewModel", result.toString())
                when(result) {
                    is uploadDownloadResource.Success -> {

                        when(val commentNotificationResponse = _userDataProvider.userData?.let {
                            _userRepo.sendCommentNotification(sender = it,
                                postId = postId,
                                comment = _postCommentMessage.value.toString(),
                                commentId = comment.commentId

                            ) }) {
                            is uploadDownloadResource.Success -> {
                                Log.d("ProfileViewModel", "Comment notification sent successfully")
                            }

                            is uploadDownloadResource.Error -> {
                                Log.d(
                                    "ProfileViewModel",
                                    "Error sending comment notification: ${commentNotificationResponse.exception}"
                                )
                            }

                            null -> {
                                Log.d("ProfileViewModel", "Comment notification sent successfully")
                            }
                        }

                        _postCommentMessage.value = "Comment Posted"
                        _postComment.value = null
                        _commentButtonState.value = false

                    }

                    is uploadDownloadResource.Error -> {
                        Log.d(
                            "ProfileViewModel",
                            "Error creating post comment: ${result.exception}"
                        )
                        _postCommentMessage.value = "Error creating comment. Please try again later"
                        _commentButtonState.value = false

                    }
                }

            }
        }catch (e:Exception){
            Log.d("ProfileViewModel", "Error creating post comment: ${e.message}")
            _commentButtonState.value = false
            _postCommentMessage.value = "Error creating comment. Please try again later"
        }
        _commentButtonState.value = false

    }


    fun fetchedUserInfoFromFirebase(){
        _country.value = _userDataProvider.userData?.country.toString()
        _city.value = _userDataProvider.userData?.city.toString()
        _chefLevel.value = _userDataProvider.userData?.chefLevel.toString()
        _specialties.value = _userDataProvider.userData?.specialties.toString()
        _goldenChefHats.intValue = _userDataProvider.userData?.goldenChefHats ?: 0

    }

    suspend fun updatePhoto(imageUri: Uri, type: String): uploadDownloadResource<Unit> {


        val downloadUrl = withContext(Dispatchers.IO) {
            _userRepo.uploadImageAndGetUrl(imageUri, type)
        }

        val result = if (type == "profile") {
            _userDataProvider.userData?.profilePictureUrl = downloadUrl
            _userRepo.uploadProfilePicture(downloadUrl.toString())
        } else {
            _userDataProvider.userData?.backGroundPictureUrl = downloadUrl
            _userRepo.uploadBackgroundPicture(downloadUrl.toString())
        }
        return result


    }


    fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) return "Unknown Date"

        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)

        return when {
            minutes < 60 -> "$minutes m"
            hours < 24 -> "$hours h"
            else -> {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }


    fun setDeletePostResult(result: String) {
        _deletePostResult.value = result
    }



    fun setShowShares(show: Boolean){
        _showShares.value = show
    }



    fun setInformation(info: Boolean){
        _information.value = info
    }

    fun setProfileManagement(edit: Boolean){
        _editProfile.value = edit
    }

    fun setCountry(country: String){
        Log.d("Country2", _userDataProvider.userData?.country.toString())
        _country.value = country
    }

    fun setCity(city: String){
        _city.value = city
    }

    fun setChefLevel(chefLevel: String){
        _chefLevel.value = chefLevel
    }

    fun setSpecialties(specialty: String){
        _specialties.value = specialty
    }




    suspend fun deletePost(post: UserMealDetailModel?) {
        if(post != null) {
            when (val result = _recipeRepo.deletePost(post)) {
                is uploadDownloadResource.Success -> {
                    _deletePostResult.value = "Post deleted successfully"
                }

                is uploadDownloadResource.Error -> {
                    _deletePostResult.value = "Error deleting post: ${result.exception}"
                }
            }
        }
    }

    fun checkIfIsProfileOwner(user: UserDataModel): Boolean{
        return user.uid == _userDataProvider.userData?.uid
    }


    fun checkFriendship() {
        viewModelScope.launch {
            val isFriend = _user.value?.let {
                _userRepo.checkFriendShip(_userDataProvider.userData!!, it)
            } ?: false
            _isFriend.value = isFriend

            _friendshipState.emit(if (isFriend) FriendshipStatus.FRIENDS else FriendshipStatus.NOT_FRIENDS)
            Log.d("FriendCheck", "Friendship status set to: $friendshipState")
        }
    }





    suspend fun saveInformation(){
        _userDataProvider.userData?.city = _city.value
        _userDataProvider.userData?.country = _country.value
        _userDataProvider.userData?.chefLevel = _chefLevel.value
        _userDataProvider.userData?.specialties = _specialties.value
        _userDataProvider.userData?.goldenChefHats = _goldenChefHats.intValue
        val result = _userRepo.saveUserInformation(_userDataProvider)


        when(result){
            is uploadDownloadResource.Success ->{
                Log.d("ProfileViewModel", "User information saved successfully")
            }
            is uploadDownloadResource.Error ->{
                _saveInfoResult.value = "Something goes wrong \n Please try to save again"
            }
        }
    }

    @Override
    override suspend fun deleteComment(comment: String, postId: String) {
        when (val response = _recipeRepo.deleteComment(comment)) {
            is uploadDownloadResource.Success -> {
                when(val commentDeleteResponse = _userRepo.deleteCommentNotification(sender = _userDataProvider.userData!!, postId = postId, commentId = comment)){
                    is uploadDownloadResource.Success -> {
                        Log.d("ProfileViewModel", "Comment notification deleted successfully")
                    }
                    is uploadDownloadResource.Error -> {
                        Log.d("ProfileViewModel", "Error deleting comment notification: ${commentDeleteResponse.exception}")
                    }
                }




                Log.d("ProfileViewModel", "Comment deleted successfully")

            }
            is uploadDownloadResource.Error -> {
                Log.d("ProfileViewModel", "Error deleting comment: ${response.exception}")

            }
        }

    }


    suspend fun deleteFriend() {
        viewModelScope.launch {
            try {
                val result = _userDataProvider.userData?.let {
                    _user.value?.let { receiver -> _userRepo.deleteFriend(it, receiver) }
                } ?: false

                if (result) {
                    _friendshipState.emit(FriendshipStatus.NOT_FRIENDS)
                }
            } catch (e: Exception) {
                Log.e("FriendRequestViewModel", "Error deleting friend: ${e.message}")
            }
        }
    }

    suspend fun sendFriendRequest() {
        viewModelScope.launch {
            try {
                val result = _userDataProvider.userData?.let {
                    _user.value?.let { receiver -> _userRepo.sendFriendRequest(it, receiver) }
                } ?: false

                if (result) {
                    _friendshipState.emit(FriendshipStatus.PENDING)
                }
            } catch (e: Exception) {
                Log.e("FriendRequestViewModel", "Error sending friend request: ${e.message}")
            }
        }
    }

    suspend fun removeFriendRequest() {
        viewModelScope.launch {
            try {
                val result = _userDataProvider.userData?.let {
                    _user.value?.let { receiver -> _userRepo.removeFriendRequest(receiver, it) }
                } ?: false

                if (result) {
                    _friendshipState.emit(FriendshipStatus.NOT_FRIENDS)
                }
            } catch (e: Exception) {
                Log.e("FriendRequestViewModel", "Error removing friend request: ${e.message}")
            }
        }
    }



    private val _comments = MutableStateFlow<List<PostCommentModel>>(emptyList())
    val comments: StateFlow<List<PostCommentModel>> get() = _comments

    fun startListeningToComments(recipeId: String) {
        if (commentLikeListeners.containsKey(recipeId)) return
        val listener = _recipeRepo.listenToCommentsForPost(recipeId) { newComments ->
            _comments.value = newComments

            _shares.value = _shares.value.map { share ->
                if (share.recipeId == recipeId) {
                    val updatedComments = newComments.map { newComment ->
                        val oldComment = share.comments?.find { it.commentId == newComment.commentId }
                        newComment.copy(
                            isLiked = oldComment?.isLiked ?: newComment.isLiked,
                            countLikes = oldComment?.countLikes ?: newComment.countLikes
                        )
                    }
                    share.copy(comments = updatedComments)
                } else {
                    share
                }
            }
        }
        commentLikeListeners[recipeId] = listener
    }




    @Override
    override fun toggleLikeForComment(commentId: String, liked: Boolean) {
        // Ενημερώνουμε την κατάσταση liked για το συγκεκριμένο σχόλιο
        val updatedLikes = _likedComments.value.toMutableMap()
        updatedLikes[commentId] = liked
        _likedComments.value = updatedLikes
    }

    private var paginationIndex = 0

    override suspend fun loadUsersWithPagination(comment: PostCommentModel?, share: UserMealDetailModel?) {
        if (_loadingState.value) {
            Log.d("Pagination", "Already loading, skipping request")
            return // μην φορτώνεις ξανά αν είναι ήδη σε loading
        }

        _loadingState.value = true
        Log.d("Pagination", "Starting to load users with pagination. Current startIndex: $paginationIndex")

        val response = if(comment?.commentId?.isNotEmpty() == true){ _recipeRepo.fetchUsersWhoLikedSpecificCommentWithPagination(
            comment = comment,
            startIndex = paginationIndex,
            pageSize = 10
        )}else{ _recipeRepo.fetchLikesForPostWithPagination(
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
                    _likedUsers.value = (_likedUsers.value ?: emptyList()) + newUsers
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
    suspend fun startListeningToRecipeUpdate(recipeId: String, onUpdate: (UserMealDetailModel) -> Unit) {
        _recipeRepo.listenToRecipeUpdates(recipeId, onUpdate)
    }



    suspend fun fetchShares(
        user: UserDataModel
    ) {
        Log.d("ProfileViewModel", "Fetching shares for user: ${user.uid}")

        val fetchedResults = _recipeRepo.fetchRecipeShares(
            user
        )
        Log.d("ProfileViewModel", "Shares fetched successfully")

        when (fetchedResults) {
            is uploadDownloadResource.Success -> {
                val shares = fetchedResults.data
                updateLikes(shares)
                val commentsResponse = _recipeRepo.fetchComments(shares)
                Log.d("CommentLikeCheck", commentsResponse.toString())

                var enrichedShares = emptyList<UserMealDetailModel>()
                val initialLikedComments = mutableMapOf<String, Boolean>()
                val currentUserId = _userDataProvider.userData?.uid

                when (commentsResponse) {
                    is uploadDownloadResource.Success -> {
                        enrichedShares = shares.map { share ->
                            val postComments = commentsResponse.data
                                .filter { it.postId == share.recipeId }
                                .sortedByDescending { it.timestamp }

                            val updatedComments = postComments.map { comment ->
                                val isLiked = comment.whoLikeIt?.contains(currentUserId) == true
                                initialLikedComments[comment.commentId] = isLiked
                                comment.copy(isLiked = isLiked)
                            }

                            share.copy(comments = updatedComments)
                        }
                    }

                    is uploadDownloadResource.Error -> {
                        Log.d("ProfileViewModel", "Error fetching comments: ${commentsResponse.exception}")
                    }
                }

                Log.d("isLikedTest1", enrichedShares.toString())

                _shares.value = enrichedShares
                _likedComments.value = initialLikedComments
                Log.d("isLikedTest2", _shares.value.toString())

                enrichedShares.forEach { share ->
                    share.recipeId?.let {
                        startListeningToCommentCount(it)
                        startListeningToCommentLikes(it)
                        startListeningPostLikesCount(it)
                    }
                }

                enrichedShares.forEach { share ->
                    val recipeId = share.recipeId ?: return@forEach

                    startListeningToCommentCount(recipeId)
                    startListeningToCommentLikes(recipeId)
                    startListeningPostLikesCount(recipeId)

                    startListeningPostLikes(recipeId) { newLikes ->
                        val currentSharesLive = _shares.value.toMutableList() ?: return@startListeningPostLikes
                        val index = currentSharesLive.indexOfFirst { it.recipeId == recipeId }

                        if (index != -1) {
                            val updatedShare = currentSharesLive[index].copy(
                                whoLikeIt = newLikes,
                                countLikes = newLikes.size,
                                isLikeButtonLoading = false
                            )
                            currentSharesLive[index] = updatedShare
                            _shares.value = currentSharesLive
                        }
                    }

                    startListeningToRecipeUpdate(recipeId) { updatedRecipe ->
                        val currentShares = _shares.value.toMutableList()
                        val index = currentShares.indexOfFirst { it.recipeId == recipeId }

                        if (index != -1) {
                            val currentShare = currentShares[index]
                            val mergedRecipe = currentShare.copy(
                                recipeName = updatedRecipe.recipeName,
                                steps = updatedRecipe.steps,
                                recipeId = currentShare.recipeId,
                                recipeIngredients = updatedRecipe.recipeIngredients,
                                recipeImage = updatedRecipe.recipeImage,
                                categoryId = updatedRecipe.categoryId,
                                visibility = updatedRecipe.visibility,
                                countComments = updatedRecipe.countComments,
                                timestamp = updatedRecipe.timestamp,
                                creatorId = updatedRecipe.creatorId,
                                isUserRecipe = updatedRecipe.isUserRecipe,
                                // preserve others
                                countLikes = currentShare.countLikes,
                                whoLikeIt = currentShare.whoLikeIt,
                                comments = currentShare.comments,
                                isLikeButtonLoading = currentShare.isLikeButtonLoading
                            )
                            currentShares[index] = mergedRecipe
                            _shares.value = currentShares
                        }
                    }
                }


                Log.d("ProfileViewModel", "Shares enriched and set")
            }

            is uploadDownloadResource.Error -> {
                Log.d("ProfileViewModel", "Error fetching shares: ${fetchedResults.exception}")
                _errorFetchingShares.value = "Something went wrong. \nPlease try again"
            }
        }
    }



}

