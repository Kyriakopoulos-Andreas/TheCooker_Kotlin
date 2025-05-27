package com.TheCooker.Domain.Layer.Models.NotificationsModels

import com.google.firebase.Timestamp

class LikeNotification (
    override var sender: String,
    override var timestamp: Timestamp?,
    override var status: String,
    override var viewStatus: String,
    @JvmField val senderImageUrl: String = "",
    @JvmField val senderEmail: String = "",
    @JvmField val postId: String = "",
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
        return "${this.sender} likes your post."
    }
    }