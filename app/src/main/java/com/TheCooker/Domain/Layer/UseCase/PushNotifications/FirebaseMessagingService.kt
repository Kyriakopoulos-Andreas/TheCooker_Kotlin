package com.TheCooker.Domain.Layer.UseCase.PushNotifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.TheCooker.DI.Module.NotificationServiceEntryPoint
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.R
import com.TheCooker.dataLayer.Repositories.UserRepo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificationFirebaseMessagingService : FirebaseMessagingService() {




    private val userRepo: UserRepo by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            NotificationServiceEntryPoint::class.java
        ).userRepo()
    }

    private val userDataProvider: UserDataProvider by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            NotificationServiceEntryPoint::class.java
        ).userDataProvider()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage){ // Εκτελείται αυτόματα όταν λάβεις push notification

        val notification = remoteMessage.notification // Παίρνει το notification object (αν υπάρχει) — για "title" & "body"
        val data = remoteMessage.data // Παίρνει τα custom data (key-value pairs) που έστειλα από τον server

        val title = notification?.title ?: data["title"] // Προσπαθεί να πάρει τον τίτλο από notification  αλλιώς από τα data
        val body = notification?.body ?: data["body"]   // Το ίδιο για το μήνυμα
        Log.d("NotificationService", "User ID: ${userRepo.auth.currentUser?.uid}")
        showNotification(title, body) // Καλω τη μέθοδο για να εμφανίσει το notification στον χρήστη
    }



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM Token", "New token received: $token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = userDataProvider.userData?.email
                if (!email.isNullOrEmpty()) {
                    userRepo.saveFcmTokenToFirestore(email, token)
                    Log.d("FCM Token", "Token saved successfully")
                } else {
                    Log.e("FCM Token", "User email is null or empty")
                }
            } catch (e: Exception) {
                Log.e("FCM Token", "Error saving token: ${e.message}")
            }
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Παίρνει τον NotificationManager του συστήματος, υπεύθυνο για να εμφανίσει ειδοποιήσεις

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "General Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for the app"
            }
            notificationManager.createNotificationChannel(channel)
        }


        val notification = NotificationCompat.Builder(this, "default_channel") // Φτιάχνει το notification
            .setContentTitle(title ?: "Notification") // Ορίζει τον τίτλο (αν είναι null, βάζει default)
            .setContentText(message ?: "") // Ορίζει το περιεχόμενο (μήνυμα)
            .setSmallIcon(R.drawable.notlog) // Ορίζει το εικονίδιο της ειδοποίησης
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Υψηλή προτεραιότητα (ειδοποίηση εμφανίζεται πάνω)
            .setAutoCancel(true) // Όταν την πατήσεις, εξαφανίζεται
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        // Δείχνει το notification με μοναδικό ID (χρησιμοποιεί το currentTimeMillis για να μην επαναλαμβάνεται)
    }
}
