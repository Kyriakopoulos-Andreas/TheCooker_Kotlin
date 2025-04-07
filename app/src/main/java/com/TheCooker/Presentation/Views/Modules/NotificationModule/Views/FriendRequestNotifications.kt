package com.TheCooker.Presentation.Views.Modules.NotificationModule.Views

import com.google.firebase.Timestamp

class FriendRequestNotifications(
    override var sender: String,
    override var timestamp: Timestamp?,
    override var status: String,
    override var viewStatus: String,
    @JvmField val senderImageUrl: String = "",
    @JvmField val senderEmail: String = ""
) : NotificationModel(sender = sender,receiverEmail = "", timestamp =timestamp, status =status, viewStatus = viewStatus, receiver = "") {

    constructor() : this(sender = "", timestamp = Timestamp.now(), status = "", viewStatus = "")
//    @Override
//    override fun toString(): String {
//        return "${this.sender.userName} has sent you a friend request!"
//    }
    override  fun getProfilePictureUrl(): String {
        return senderImageUrl
    }

    fun getSenderEmail(): String {
        return senderEmail
    }

    @Override
    override fun toString(): String {
        return "${this.sender} has sent you a friend request."
    }
}