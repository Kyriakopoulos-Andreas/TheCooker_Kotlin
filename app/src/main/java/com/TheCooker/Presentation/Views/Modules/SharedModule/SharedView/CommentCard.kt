package com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun CommentCard(share: UserMealDetailModel,
                profileViewModel: ProfileViewModel,
                scope: CoroutineScope,
                homeViewModel: HomeViewModel?,
                @ApplicationContext  context: Context,
                navigator: NavController,
                showModal: () -> Unit,
                fetchCommentForDeleteOrUpdate: (PostCommentModel?) -> Unit
) {



    Column(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(max = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            share.comments?.let { comments ->
                items(comments.size) { index ->
                    val comment = comments[index]


                    val likedCommentsMap = if (homeViewModel != null) {
                        homeViewModel.commentsLikes.collectAsState().value
                    } else {
                        profileViewModel.likedComments.collectAsState().value
                    }

                    val liked = likedCommentsMap[comment.commentId] ?: false
                    val heartColor = if (liked) colorResource(id = R.color.yellow) else Color.White


                    Log.d("CommentOnCommentCard", comment.toString())

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.lightGrey))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = comment.senderObj?.profilePictureUrl,
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = comment.senderObj?.userName ?: "",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                                    color = Color.White,
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(onClick = {
                                    fetchCommentForDeleteOrUpdate(comment)
                                    showModal()
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.menu_post),
                                        contentDescription = "Menu Icon",
                                        tint = colorResource(id = R.color.yellow)
                                    )
                                }
                            }


                            Spacer(modifier = Modifier.height(8.dp))


                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = comment.comment,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )

                                Box(
                                    modifier = Modifier.weight(0.2f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        IconButton(onClick = {
                                            if (!isInternetAvailable(context)) return@IconButton

                                            scope.launch(Dispatchers.IO) {

                                                val result = if (!liked) {
                                                    (homeViewModel ?: profileViewModel).commentLike(
                                                        comment
                                                    )
                                                } else {
                                                    (homeViewModel
                                                        ?: profileViewModel).unLikeComment(comment)
                                                }


                                                withContext(Dispatchers.Main) {
                                                    if (result) {
                                                        (homeViewModel
                                                            ?: profileViewModel).toggleLikeForComment(
                                                            comment.commentId,
                                                            !liked
                                                        )
                                                    } else {
                                                        val errorMessage = if (!liked) {
                                                            "Error liking comment"
                                                        } else {
                                                            "Error unliking comment"
                                                        }
                                                        Toast.makeText(
                                                            context,
                                                            errorMessage,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        })
                                        {

                                            Icon(
                                                imageVector = if (liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                                contentDescription = "Like",
                                                tint = heartColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Text(
                                            text = comment.countLikes.toString(),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall,
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
                                                        "comment",
                                                        comment
                                                    )
                                                    navigator.currentBackStackEntry?.savedStateHandle?.set(
                                                        "share",
                                                        share
                                                    )
                                                    navigator.navigate("CommentLikesView")
                                                } else {
                                                    navigator.currentBackStackEntry?.savedStateHandle?.set(
                                                        "source",
                                                        "profile"
                                                    )
                                                    navigator.currentBackStackEntry?.savedStateHandle?.set(
                                                        "comment",
                                                        comment
                                                    )
                                                    navigator.currentBackStackEntry?.savedStateHandle?.set(
                                                        "share",
                                                        share
                                                    )
                                                    navigator.navigate("CommentLikesView")
                                                }


                                            }
                                        )
                                    }

                                }
                            }


                            Spacer(modifier = Modifier.height(4.dp))


                            Text(
                                text = profileViewModel.formatTimestamp(comment.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            } ?: item {
                Text("No comments yet.")
            }
        }

    }



}


@Composable
fun CommentSettingsBottomSheetContent(
    modifier: Modifier,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit,
    isUserComment: Boolean
) {
    Log.d("PostMenuBottomSheetContent", "Called")
    if (isUserComment) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 120.dp)
                .background(color = Color(0xFF202020))
        ) {
            Column(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.update_recipe),
                        contentDescription = "Update Comment",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Update Comment",
                        fontSize = 21.sp,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.clickable { onUpdateClick() }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.outline_delete_24),
                        contentDescription = "Delete Comment",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Delete Comment",
                        fontSize = 21.sp,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.clickable { onDeleteClick() }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 120.dp)
                .background(color = Color(0xFF202020))
        ) {
            Column(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.report),
                        contentDescription = "Update Comment",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Report Comment",
                        fontSize = 21.sp,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.clickable { }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.hide),
                        contentDescription = "Hide Comment",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Hide Comment",
                        fontSize = 21.sp,
                        color = Color(0xFFFFC107),
                        modifier = Modifier.clickable { onDeleteClick() }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

}