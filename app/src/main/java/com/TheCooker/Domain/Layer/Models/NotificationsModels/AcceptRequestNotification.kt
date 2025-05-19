package com.TheCooker.Domain.Layer.Models.NotificationsModels

import com.google.firebase.Timestamp

class AcceptRequestNotification(
    receiverEmail: String = "",
    timestamp: Timestamp? = Timestamp.now(),
    viewStatus: String = "",
    receiver: String = "",
    status: String = "",
    @JvmField val receiverImageUrl: String = ""
) : NotificationModel( receiverEmail =receiverEmail, timestamp = timestamp, viewStatus = viewStatus, status = status, receiver = receiver) {

    @Override
    override fun toString(): String {
        return "${this.receiver} has accepted your friend request! Start discovering their delicious recipes now."
    }

    override  fun getProfilePictureUrl(): String {
        return receiverImageUrl
    }




}




