package com.TheCooker.Domain.Layer.Models.NotificationsModels

import com.google.firebase.Timestamp

class CommentNotification (
    override var sender: String,
    override var timestamp: Timestamp?,
    override var status: String,
    override var viewStatus: String,
    @JvmField val senderImageUrl: String = "",
    @JvmField val senderEmail: String = "",
    @JvmField val postId: String = "",
    @JvmField val comment: String = "",
    @JvmField val commentId: String = ""
) : NotificationModel(sender = sender,receiverEmail = "", timestamp =timestamp, status =status, viewStatus = viewStatus, receiver = "") {

    constructor() : this(sender = "", timestamp = Timestamp.now(), status = "", viewStatus = "")

    override  fun getProfilePictureUrl(): String {
        return senderImageUrl
    }

    fun getSenderEmail(): String {
        return senderEmail
    }

    @Override
    override fun toString(): String {
        return "${this.sender} comment to tour post: ${this.comment}"
    }
}