package com.TheCooker.Presentation.Views.Modules.SharedModule

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class CommonActionLikePost: ViewModel() {

    abstract suspend fun commentLike(comment: PostCommentModel): Boolean
    abstract suspend fun unLikeComment(comment: PostCommentModel): Boolean
    abstract fun toggleLikeForComment(commentId: String, liked: Boolean)
    abstract suspend fun postLike(share: UserMealDetailModel): Boolean
    abstract suspend fun unLikePost(share: UserMealDetailModel): Boolean
    abstract fun updateLikes(posts: List<UserMealDetailModel>)
    abstract fun togglePostLike(share: UserMealDetailModel)
    abstract fun toggleLikeForPostSupport(postId: String, liked: Boolean)
    abstract suspend fun loadUsersWithPagination(comment: PostCommentModel?, share: UserMealDetailModel?)
    abstract  fun resetLikedUsers()
    abstract fun setOpenCommentsPostId(postId: String?)
    abstract fun getOpenCommentsPostId(): String?
    abstract suspend fun deleteComment(comment: String)
    abstract fun setCommentToBeDeletedOrUpdated(comment: PostCommentModel?)
    abstract fun getCommentToBeDeletedOrUpdated(): PostCommentModel?
    abstract suspend fun updateComment(comment: PostCommentModel)
    abstract fun checkIfIsUserComment(): Boolean
    abstract  fun startListeningToPostLikes(recipeId: String)


    fun setUpdateComment(comment: String?){
        _updateComment.value = comment
    }
    fun getUpdateComment(): String?{
        return _updateComment.value
    }

    protected val _postLikes = MutableStateFlow<Map<String?, Boolean>>(emptyMap())
    val postLikes: StateFlow<Map<String?, Boolean>> get() = _postLikes

    protected val _likedUsers = MutableStateFlow<List<UserDataModel>>(emptyList())
    val likedUsers: StateFlow<List<UserDataModel>> get() = _likedUsers
    protected abstract val _openCommentsPostId: MutableStateFlow<String?>
    val openCommentsPostId: StateFlow<String?> get() = _openCommentsPostId

    protected abstract val commentTobeDeletedOrUpdated: MutableState<PostCommentModel?>
    val commentToBeDeletedOrUpdate: State<PostCommentModel?> get() = commentTobeDeletedOrUpdated

    protected val _updateComment: MutableState<String?> = mutableStateOf("")
    val updateComment: State<String?> get() = _updateComment
    protected val likePostNumberListeners = mutableMapOf<String, ListenerRegistration>()

    protected var lastVisibleUser: DocumentSnapshot? = null

    protected val _loadingState = MutableStateFlow<Boolean>(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState
}