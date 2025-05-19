package com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentUpdateView(
    comment: PostCommentModel?,
    profileViewModel: ProfileViewModel?,
    homeViewModel: HomeViewModel?,
    navController: NavController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel = (profileViewModel ?: homeViewModel) ?: return
    LaunchedEffect(comment?.commentId) {
        comment?.comment?.let {
            viewModel.setUpdateComment(it)
        }
    }

    val updateComment = viewModel.updateComment.value ?: ""

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            comment?.senderObj?.profilePictureUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "User Picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Column με TextField + Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.End
            ) {
                if (comment != null) {
                    OutlinedTextField(
                        value = updateComment,
                        onValueChange = {
                            viewModel.setUpdateComment(it)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        label = { Text(text = "") },
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
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { /* Cancel */ },
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.border(
                                width = 3.dp,
                                color = colorResource(id = R.color.ProfileCameraAssetBackground),
                                shape = RoundedCornerShape(0.dp)
                            )
                        ) {
                            Text("Cancel")
                        }


                        Button(
                            onClick = {
                                scope.launch {
                                    if(!isInternetAvailable(context)){
                                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                                        return@launch
                                    }

                                    viewModel.updateComment(comment)
                                    navController.popBackStack()


                                }
                            },
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(id = R.color.yellow), // Yellow
                                contentColor = Color.White,

                            ),
                  
                        ) {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

