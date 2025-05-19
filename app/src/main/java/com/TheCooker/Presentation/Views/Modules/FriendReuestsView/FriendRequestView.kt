package com.TheCooker.Presentation.Views.Modules.FriendReuestsView

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
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
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Presentation.Views.Modules.Dividers.ThinYellowDivider
import com.TheCooker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun FriendRequestView(){
    val viewModel = hiltViewModel<FriendRequestViewModel>()
    val scope = rememberCoroutineScope();
    var suggestions by remember { mutableStateOf<List<UserDataModel>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var friendRequests by remember { mutableStateOf<List<UserDataModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        if(!isInternetAvailable(context)){
            errorMessage = "No internet connection. Please check your network settings."
        }else{
            viewModel.fetchFriendSuggestions()
            viewModel.fetchFriendRequests()
        }

    }

    scope.launch {
    viewModel.friendSuggestions.collect { suggestion ->
        when (suggestion) {
            is uploadDownloadResource.Success -> {
                suggestions = suggestion.data

            }
            is uploadDownloadResource.Error -> {
                errorMessage = suggestion.exception?.message

            }
            null -> {
                errorMessage = "Oops! We couldn't load your friend suggestions. Tap 'Reload' to try again."

            }
        }
    }
    }

    scope.launch {
        viewModel.friendRequests.collect{ request ->
            when(request){
                is uploadDownloadResource.Success-> {
                    friendRequests= request.data
                }
                is uploadDownloadResource.Error -> {
                    errorMessage = request.exception?.message
                }
                null -> {
                    friendRequests = emptyList()
                }

            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {


        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Expand Your Cooking Circle",
                style = TextStyle(color = Color.White),
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            ThinYellowDivider()
        }

        if (friendRequests.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Friend Requests",
                    style = TextStyle(color = Color.White),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(friendRequests.size) { index ->
                PendingRequest(pendingRequest = friendRequests[index], scope, viewModel, context)
            }
            item{
                ThinYellowDivider()
            }

        }



        if(suggestions.isNotEmpty()) {
            item {
                Log.d("FriendRequestView", "Suggestions: $suggestions")
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Suggestions",
                    style = TextStyle(color = Color.White),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(suggestions.size) { index ->
                FriendRequestItem(suggestion = suggestions[index], scope, viewModel, context)
            }
        }else{
            item {
                NoSuggestions(modifier = Modifier.fillParentMaxHeight())
            }
        }
    }
}

@Composable
fun NoSuggestions(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No suggestions for now, please try again later.",
            style = TextStyle(color = Color.White),
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

    }
}


@Composable
fun PendingRequest(pendingRequest: UserDataModel, scope: CoroutineScope, viewModel: FriendRequestViewModel, context: Context) {
    // Αποθήκευση της κατάστασης για το κάθε suggestion
    val friendRequestSentState = viewModel.friendRequestSentState.collectAsState()

    // Χρησιμοποιούμε το userId ή email για να κρατάμε ξεχωριστά την κατάσταση για κάθε χρήστη
    val friendRequestSent = friendRequestSentState.value[pendingRequest.email] ?: false

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
                    .border(3.dp, Color(0xFF292929), CircleShape)
            ) {
                AsyncImage(
                    model = pendingRequest.profilePictureUrl,
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
                pendingRequest.userName?.let {
                    Text(text = it, fontSize = 18.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.padding(bottom = 24.dp))

                // Εμφανίζουμε τα κουμπιά με βάση την κατάσταση του friendRequestSent
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
                                    viewModel.acceptFriendRequest(pendingRequest)
                                    Log.d("FriendRequestItem", "Friend request accepted for ${pendingRequest.email}")
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
                                    viewModel.rejectFriendRequest(pendingRequest)
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
fun FriendRequestItem(suggestion: UserDataModel, scope: CoroutineScope, viewModel: FriendRequestViewModel, context: Context) {
    // Αποθήκευση της κατάστασης για το κάθε suggestion
    val friendRequestSentState = viewModel.friendRequestSentState.collectAsState()
    var isSending by remember { mutableStateOf(false) }
    // Χρησιμοποιούμε το userId ή email για να κρατάμε ξεχωριστά την κατάσταση για κάθε χρήστη
    val friendRequestSent = friendRequestSentState.value[suggestion.email] ?: false

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
                    .border(3.dp, Color(0xFF292929), CircleShape)
            ) {
                AsyncImage(
                    model = suggestion.profilePictureUrl,
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
                suggestion.userName?.let {
                    Text(text = it, fontSize = 18.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.padding(bottom = 24.dp))

                // Εμφανίζουμε τα κουμπιά με βάση την κατάσταση του friendRequestSent
                Row(
                    modifier = Modifier.padding(start = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Αν δεν έχει σταλεί το αίτημα, εμφανίζουμε το κουμπί "Add Friend"
                    if (!friendRequestSent) {
                        Button(
                            onClick = {
                                if(!isInternetAvailable(context)){
                                    Toast.makeText(context, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                scope.launch {
                                    isSending = true
                                    Log.d("FriendRequestItem", "Sending friend request for ${suggestion.email}")
                                    viewModel.sendFriendRequest(suggestion)
                                    isSending = false
                                }

                            },
                            enabled = !isSending,
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
                                "Add Friend",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal
                                )
                            )
                        }

                        // Κουμπί "Remove" αν το αίτημα έχει σταλεί
                        Button(
                            onClick = {
                                viewModel.removeSuggestion(suggestion.email.toString())
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = colorResource(id = R.color.ProfileCameraAssetBackground)
                            ),
                            modifier = Modifier
                                .width(130.dp)
                                .height(33.dp),
                            enabled = !isSending,
                        ) {
                            Text(
                                "Remove",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal
                                )
                            )
                        }
                    } else {
                        // Εμφανίζουμε μόνο το κουμπί "Remove" αν έχει σταλεί το αίτημα
                        Button(
                            onClick = {
                                if(!isInternetAvailable(context)){
                                    Toast.makeText(context, "No internet connection. Please check your network settings.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                scope.launch {
                                    viewModel.removeFriendRequest(suggestion)
                                }},
                            shape = RoundedCornerShape(8.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = colorResource(id = R.color.ProfileCameraAssetBackground)
                            ),
                            modifier = Modifier
                                .width(260.dp)
                                .height(33.dp)
                        ) {
                            Text(
                                "Remove",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Normal
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


