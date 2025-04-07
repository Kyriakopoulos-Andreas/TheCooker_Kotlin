package com.TheCooker.Presentation.Views.Modules.NotificationModule.Views

import com.google.firebase.Timestamp

abstract class NotificationModel(
    open var sender: String = "",  // Προσθήκη default τιμών
    open var receiverEmail: String = "",
    open var receiver: String,// Προσθήκη default τιμών
    open var timestamp: Timestamp? = Timestamp.now(), // Προσθήκη default τιμών
    open var status: String = "", // Προσθήκη default τιμών
    open var viewStatus: String = "" // Προσθήκη default τιμών
) {
    override fun toString(): String {
        return "NotificationModel(sender=$sender, receiver=$receiverEmail, receiver=$receiver, timestamp=$timestamp, status='$status', viewStatus='$viewStatus')"
    }

    open fun getProfilePictureUrl(): String? {
        return null
    }


}