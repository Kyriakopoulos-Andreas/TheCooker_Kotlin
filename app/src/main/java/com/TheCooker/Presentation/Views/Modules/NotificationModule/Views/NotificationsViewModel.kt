package com.TheCooker.Presentation.Views.Modules.NotificationModule.Views

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.NotificationsModels.AcceptRequestNotification
import com.TheCooker.Domain.Layer.Models.NotificationsModels.FriendRequestNotifications
import com.TheCooker.Domain.Layer.Models.NotificationsModels.NotificationModel
import com.TheCooker.dataLayer.Repositories.UserRepo
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val userDataProvider: UserDataProvider,
): ViewModel() {

    private val _notifications = MutableStateFlow<uploadDownloadResource<List<NotificationModel>>>(
        uploadDownloadResource.Success(emptyList())
    )
    val notification: StateFlow<uploadDownloadResource<List<NotificationModel>>> = _notifications.asStateFlow()
    private val _userWhoSendTheFriendRequest = MutableStateFlow<LoginResults<UserDataModel>>(LoginResults.Success(UserDataModel()))
    val userWhoSendTheFriendRequest: StateFlow<LoginResults<UserDataModel>> = _userWhoSendTheFriendRequest.asStateFlow()

    private val _unreadCount = MutableStateFlow<Int>(0)
    val unreadCount: StateFlow<Int> get() = _unreadCount

    fun removeNotification(notification: NotificationModel) {
        Log.d("NotificationsViewModelBef", "Removing notification: $notification")
        val currentFriendRequests = _notifications.value
        if (currentFriendRequests is uploadDownloadResource.Success) {
            Log.d("NotificationsViewModel", "Removing notification: $notification")
            when(notification){
                is FriendRequestNotifications -> {
                    val updatedList = currentFriendRequests.data.filterNot { it == notification }
                    _notifications.value = uploadDownloadResource.Success(updatedList)
                }
            }
        }

    }


    fun formatTimeAgo(timestamp: Timestamp): String {
        val timeMillis = timestamp.toDate().time
        val now = System.currentTimeMillis()
        val diff = now - timeMillis

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "just now"
            minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
            else -> "$days day${if (days != 1L) "s" else ""} ago"
        }
    }



    suspend fun fetchUserWhoSentTheFriendRequest(email: String): LoginResults<UserDataModel> {
        return try {
            userRepo.getUserDetails(email).also { result ->
                _userWhoSendTheFriendRequest.value = result
            }
        } catch (e: Exception) {
            LoginResults.Error(e).also { _userWhoSendTheFriendRequest.value = it }
        }
    }


    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val notifications =
                    userDataProvider.userData?.let {
                        userRepo.fetchNotifications(it) { unreadCount ->
                            _unreadCount.value = unreadCount as Int // Ενημέρωση unread count
                            Log.d("NotificationCount", unreadCount.toString())
                        }
                    }

                // Αν οι ειδοποιήσεις είναι διαθέσιμες
                notifications?.forEach { notification ->
                    // Safe casting για να καλέσεις την getProfilePictureUrl() μόνο στις AcceptRequestNotifications
                    if (notification is AcceptRequestNotification) {
                        val profilePictureUrl = notification.getProfilePictureUrl()
                        Log.d("NotificationsViewModel", "Profile Picture URL: $profilePictureUrl")
                    } else {
                        Log.d("NotificationsViewModel", "Notification does not have a profile picture URL")
                    }
                }

                if (notifications != null) {
                    _notifications.value =
                        uploadDownloadResource.Success(notifications) // Επιτυχία
                }
            } catch (e: Exception) {
                _notifications.value = uploadDownloadResource.Error(e)
            }
        }
    }
}

