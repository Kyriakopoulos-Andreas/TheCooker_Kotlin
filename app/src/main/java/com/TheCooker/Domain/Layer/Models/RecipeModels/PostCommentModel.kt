package com.TheCooker.Domain.Layer.Models.RecipeModels

import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import java.io.Serializable


data class PostCommentModel (
    var postId: String = "",
    var comment: String= "",
    var commentId: String = "",
    var senderId: String= "",
    var timestamp: Long= 0,
    var replies: MutableList<PostCommentModel>? = null,
    var countLikes: Int? = 0,
    var whoLikeIt: MutableList<String>?= null,
    var senderObj: UserDataModel?,
    var isLiked: Boolean = false
) : Serializable{
    constructor() : this("", "", "", "", 0, null, 0, null, null)

}