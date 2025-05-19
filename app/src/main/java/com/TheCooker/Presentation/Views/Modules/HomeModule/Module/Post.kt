package com.TheCooker.Presentation.Views.Modules.HomeModule.Module

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.Dividers.ThinYellowDivider
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView.CommentCard
import com.TheCooker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Post(
    share: UserMealDetailModel,
    userName: String,
    imageUri: String? = null,
    profileViewModel: ProfileViewModel,
    navigator: NavController,
    showModal: () -> Unit,
    onLikeClick: (Boolean) -> Unit,
    scope: CoroutineScope,
    fetchCommentIdForDeleteOrUpdate: (PostCommentModel?) -> Unit,
    showModalForCommentSettings : () -> Unit,
    showModalForPost : (UserMealDetailModel) -> Unit
) {

    Post(
        share = share,
        userName = userName,
        imageUri = imageUri,
        profileViewModel = profileViewModel,
        navigator = navigator,
        showModal = showModal,

        onLikeClick = onLikeClick,
        homeViewModel = null,
        scope = scope,
        fetchCommentForUpdateOrDelete = fetchCommentIdForDeleteOrUpdate,
        showModalForCommentSettings = showModalForCommentSettings,
        showModalForPost = showModalForPost
    )
}



@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Post(
    share: UserMealDetailModel,
    userName: String,
    imageUri: String? = null,
    profileViewModel: ProfileViewModel,
    navigator: NavController,
    showModal: () -> Unit,
    onLikeClick: (Boolean) -> Unit,
    homeViewModel: HomeViewModel?,
    scope: CoroutineScope,
    fetchCommentForUpdateOrDelete: (PostCommentModel?) -> Unit,
    showModalForCommentSettings : () -> Unit,
    showModalForPost : (UserMealDetailModel) -> Unit

) {


    Log.d("PostWhoLikeIt", share.whoLikeIt.toString())










    Log.d("ViewModelsOnPost", "$profileViewModel $homeViewModel")

    val liveCommentCount by rememberUpdatedState(newValue = share.countComments)
    Log.d("Post", share.countComments.toString())
    Log.d("Post", share.toString())
    var isCommentFieldFocused by remember { mutableStateOf(true) }


    var shareTarget by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("Write Comment") }
    val viewModel = homeViewModel ?: profileViewModel
    val openCommentsPostId by viewModel.openCommentsPostId.collectAsState()
    var showComments by remember { mutableStateOf(openCommentsPostId == share.recipeId) }

    LaunchedEffect(openCommentsPostId) {
        showComments = openCommentsPostId == share.recipeId
    }


      val postLikes =  viewModel.postLikes.collectAsState().value

        profileViewModel.postLikes.collectAsState().value



    val liked1 = postLikes[share.recipeId] ?: false


    val heartColor = if (liked1) colorResource(id = R.color.yellow) else Color.White



    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(share.recipeId) {
        share.recipeId?.let { profileViewModel.startListeningToComments(it)

        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.darkGrey),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(4.dp)
            .shadow(2.dp, shape = RoundedCornerShape(8.dp))
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = userName,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = profileViewModel.formatTimestamp(share.timestamp),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray
                        )
                    )
                }
            }

            // Menu Icon
            IconButton(onClick = {
                showModalForPost(
                    share
                )
            } ){
                Icon(
                    painter = painterResource(id = R.drawable.menu_post),
                    contentDescription = "Menu Icon",
                    tint = colorResource(id = R.color.yellow)
                )
            }
        }

        val imageModel = if (share.recipeImage == null || share.recipeImage == "android.resource://com.TheCooker/2131165489") {
            R.drawable.testmeal
        } else {
            share.recipeImage
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = imageModel,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxSize()
                    .height(180.dp)
                    .clickable {
                        Log.d("ShareOnClick", share.toString())
                        navigator.currentBackStackEntry?.savedStateHandle?.set("PostRecipe", share)

                        navigator.navigate("MealDetailView")
                    },
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))



        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = share.recipeName ?: "No Recipe Name",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ThinYellowDivider()

        // Footer: Like (Heart Icon), Comment, Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Heart Button for Like
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    Log.d("PostWhoLikeIt", share.whoLikeIt.toString())
                    viewModel.togglePostLike(share)

                }) {

                    Icon(
                        imageVector = if (liked1) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, // Χρησιμοποιούμε το εικονίδιο καρδιάς
                        contentDescription = "Like",
                        tint = heartColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = share.countLikes.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    modifier = Modifier.clickable {
                        if(!isInternetAvailable(context = context)){
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                            return@clickable
                        }


                        if (homeViewModel != null) {
                            navigator.currentBackStackEntry?.savedStateHandle?.set(
                                "source",
                                "home"
                            )
                            navigator.currentBackStackEntry?.savedStateHandle?.set(
                                "post",
                                share
                            )

                            navigator.navigate("PostLikesView")
                        } else {
                            navigator.currentBackStackEntry?.savedStateHandle?.set(
                                "source",
                                "profile"
                            )
                            navigator.currentBackStackEntry?.savedStateHandle?.set(
                                "post",
                                share
                            )
                            navigator.navigate("PostLikesView")
                        }

                    })
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val commentsColor =
                    if (showComments) colorResource(id = R.color.yellow) else Color.White
                val countColor = if (showComments) colorResource(id = R.color.yellow) else Color.Gray

                IconButton(onClick = {
                    if (showComments) {
                        viewModel.setOpenCommentsPostId(null)
                    } else {
                        viewModel.setOpenCommentsPostId(share.recipeId)
                    }
                }){

                    Icon(
                        painter = painterResource(id = R.drawable.rounded_comment_24),
                        contentDescription = "Comment",
                        modifier = Modifier.size(24.dp),
                        tint = countColor
                    )
                }

                Text(
                    text = liveCommentCount.toString(),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = commentsColor
                    )
                )





            }


            // Share Button
            IconButton(onClick = { shareTarget = !shareTarget }) {
                val shareColor =
                    if (shareTarget) colorResource(id = R.color.yellow) else Color.White
                Icon(
                    painter = painterResource(id = R.drawable.baseline_share_24),
                    contentDescription = "Share",
                    modifier = Modifier.size(24.dp),
                    tint = shareColor
                )
            }
        }

        AnimatedVisibility(visible = showComments) {
            Column(modifier = Modifier.padding(top = 0.dp, bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    profileViewModel.postCommentMessage.value?.let {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            label = { Text("") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(60.dp)
                                .focusRequester(focusRequester)
                                .onFocusChanged { focusState: FocusState ->
                                    isCommentFieldFocused = focusState.isFocused
                                    if (!isCommentFieldFocused && commentText.isBlank()) {
                                        commentText = "Write Comment"
                                    } else if (isCommentFieldFocused && commentText == "Write Comment") {
                                        commentText = ""
                                    }
                                },
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (!isInternetAvailable(context = context)) {

                                        return@IconButton
                                    } else {
                                        scope.launch {
                                            profileViewModel.onPostCommentMessageChange(commentText)
                                            Log.d("Post1", homeViewModel.toString())

                                            share.recipeId?.let { recipeId ->
                                                profileViewModel.createPostComment(recipeId)
                                                    if(profileViewModel.postCommentResult.value != null){
                                                        Toast.makeText(context, profileViewModel.postCommentResult.value, Toast.LENGTH_SHORT).show()
                                                    }

                                            }

                                            if (profileViewModel.postCommentMessage.value == "Comment Posted")
                                                commentText = "Write Comment"
                                            focusManager.clearFocus()
                                        }
                                    }

                                },
                                    enabled = !profileViewModel.commentButtonState.value) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.post_comment),
                                        contentDescription = "Share",
                                        modifier = Modifier.size(24.dp),
                                        tint = colorResource(id = R.color.yellow)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xAAFFC107),
                                cursorColor = Color(0xFFFFC107),
                                focusedLabelColor = Color(0xFFFFC107),
                                unfocusedLabelColor = Color(0xFFFFC107),
                                focusedTextColor = Color(0xFFFFC107),
                                unfocusedTextColor = Color(0xAAFFC107),
                                containerColor = Color.Transparent


                            ),
                            shape = RoundedCornerShape(32.dp),
                        )
                    }
                }
                Log.d("likesOnPost", share.comments.toString())
                CommentCard(share, profileViewModel, scope, homeViewModel, context, navigator, showModalForCommentSettings, fetchCommentForUpdateOrDelete)
            }

        }
    }
}