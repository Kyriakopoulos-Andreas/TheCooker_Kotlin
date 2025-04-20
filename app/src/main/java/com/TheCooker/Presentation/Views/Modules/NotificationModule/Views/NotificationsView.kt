package com.example.cooker.ChatView

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.Presentation.Views.Modules.FriendReuestsView.FriendRequestViewModel
import com.TheCooker.Domain.Layer.Models.NotificationsModels.AcceptRequestNotification
import com.TheCooker.Domain.Layer.Models.NotificationsModels.FriendRequestNotifications
import com.TheCooker.Presentation.Views.Modules.NotificationModule.Views.NotificationsViewModel
import com.TheCooker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NotificationView(){

    val notificationsViewModel = hiltViewModel<NotificationsViewModel>()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val notifications = notificationsViewModel.notification.collectAsState()
    val scope = rememberCoroutineScope()
    val friendRequestViewModel = hiltViewModel<FriendRequestViewModel>()



    val notificationsList = when (val result = notifications.value) {
        is uploadDownloadResource.Success -> result.data
        is uploadDownloadResource.Error -> {
            errorMessage.value = result.exception?.message
            emptyList()
        }

    }

    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)){
            errorMessage.value = "No internet connection. Please check your network settings."
        }else{
            notificationsViewModel.fetchNotifications()
        }

    }

    LazyColumn(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Notifications",
                style = TextStyle(color = Color.White),
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (notificationsList.isEmpty()) {
            item{
            NoNotificationsItem()}
        } else {
            items(notificationsList.size) { index ->
                val notification = notificationsList[index]
                when (notification) {
                    is AcceptRequestNotification -> AcceptRequestNotificationItem(notification, notificationsViewModel)
                    is FriendRequestNotifications -> FriendRequestNotificationItem(
                        notification = notification,
                        scope = scope,
                        friendRequestViewModel = friendRequestViewModel,
                        context = context,
                        notificationViewModel = notificationsViewModel
                    )
                }
            }
        }

    }


}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FriendRequestNotificationItem(notification: FriendRequestNotifications, scope: CoroutineScope, friendRequestViewModel
: FriendRequestViewModel, context: Context, notificationViewModel: NotificationsViewModel){
    val friendRequestAccepted = friendRequestViewModel.friendRequestAcceptedState.collectAsState()
    var wasAccepted by remember { mutableStateOf(false) }
    val friendRequestAcceptState = friendRequestViewModel.friendRequestAcceptedState.collectAsState()

    LaunchedEffect(friendRequestAcceptState.value, wasAccepted) {
        if (friendRequestAcceptState.value && wasAccepted) {
            notificationViewModel.removeNotification(notification)
            friendRequestViewModel.initFriendRequestAcceptState()
            wasAccepted = false // reset το flag για ασφάλεια
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)

            ) {
                AsyncImage(
                    model = notification.getProfilePictureUrl(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = notification.toString(),
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(start = 12.dp))

                val timeAgo = remember(notification.timestamp) { notification.timestamp?.let {
                    notificationViewModel.formatTimeAgo(
                        it
                    )
                } }

                if (timeAgo != null) {
                    Text(
                        text = timeAgo,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(bottom = 16.dp))


                Row(
                    modifier = Modifier.padding(start = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {

                    Button(
                        onClick = {
                            if (!isInternetAvailable(context)) {
                                Toast.makeText(
                                    context,
                                    "No internet connection. Please check your network settings.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            scope.launch {
                                val result = notificationViewModel.fetchUserWhoSentTheFriendRequest(notification.senderEmail)

                                if (result is LoginResults.Success) {
                                    val user = result.data
                                    wasAccepted = true //
                                    friendRequestViewModel.acceptFriendRequest(user)

                                } else if (result is LoginResults.Error) {
                                    Log.d("FriendRequest", "Error fetching user: ${result.exception?.message}")
                                }
                            }

                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = colorResource(id = R.color.yellow)
                        ),
                        modifier = Modifier
                            .width(130.dp)
                            .height(33.dp)
                    ) {
                        Text(
                            "Accept",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal
                            )
                        )
                    }


                    Button(
                        onClick = {
                            if (!isInternetAvailable(context)) {
                                Toast.makeText(
                                    context,
                                    "No internet connection. Please check your network settings.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            scope.launch {
                                val result = notificationViewModel.fetchUserWhoSentTheFriendRequest(notification.senderEmail)
                                if(result is LoginResults.Success){
                                    val user = result.data
                                    val res = friendRequestViewModel.rejectFriendRequest(user)
                                    Log.d("FriendRequest", "User data: $user")
                                    if(res){
                                        Log.d("FriendRequest", "Friend request rejected")
                                        notificationViewModel.removeNotification(notification)
                                    }
                                }
                            }

                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = colorResource(id = R.color.ProfileCameraAssetBackground)
                        ),
                        modifier = Modifier
                            .width(130.dp)
                            .height(33.dp)
                    ) {
                        Text(
                            "Decline",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Normal
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

}









@Composable
fun AcceptRequestNotificationItem(notification: AcceptRequestNotification, notificationViewModel: NotificationsViewModel){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                   
            ) {
                AsyncImage(
                    model = notification.getProfilePictureUrl(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable {
                    // Handle notification click
                }) {
                Text(
                    text = notification.toString(),
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            val timeAgo = remember(notification.timestamp) { notification.timestamp?.let {
                notificationViewModel.formatTimeAgo(
                    it
                )
            } }

            if (timeAgo != null) {
                Text(
                    text = timeAgo,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 16.dp))

        }
    }

}

@Composable
fun NoNotificationsItem(){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)

            ) {
                AsyncImage(
                    model = R.drawable.logo_white,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clickable {
                    // Handle notification click
                }) {
                Text(
                    text = "No notifications yet — your kitchen is quiet for now",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.padding(bottom = 24.dp))

        }
    }

}
