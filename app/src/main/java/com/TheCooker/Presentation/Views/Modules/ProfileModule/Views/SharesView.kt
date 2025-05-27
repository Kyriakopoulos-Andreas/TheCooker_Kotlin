package com.TheCooker.Presentation.Views.Modules.ProfileModule.Views

import android.util.Log
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
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.Dividers.BlackFatDivider
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.Post
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.R


@Composable
fun SharesView(
    profileViewModel: ProfileViewModel,
    showModal: (UserMealDetailModel?) -> Unit,
    hideModal: () -> Unit,
    navigator: NavController,
    showModalForCommentSettings : () -> Unit,
    fetchCommentIdForDeleteOrUpdate: (PostCommentModel?) -> Unit,


    ) {




    var selectedShare by remember { mutableStateOf<UserMealDetailModel?>(null) }
    var modalTrigger by remember { mutableStateOf(0) }

    fun onShareClick(share: UserMealDetailModel) {
        selectedShare = share
        modalTrigger++ // Force refresh
    }

    LaunchedEffect(modalTrigger) {
        if (selectedShare != null) {
            showModal(selectedShare)
        } else {
            hideModal()
        }
    }



    val shares by profileViewModel.shares.collectAsState()
    Log.d("SharesView2", shares.toString())




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.darkGrey))
            .padding(top = 10.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (shares.isEmpty()) {
            Text(
                text = profileViewModel.errorFetchingShares.value,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {


            shares.forEach { share ->



                Log.d("ShareViewLikeTest", share.toString())

                Post(
                    share = share,
                    userName = profileViewModel.user.value?.userName ?: "Unknown",
                    imageUri = profileViewModel.user.value?.profilePictureUrl,
                    profileViewModel = profileViewModel,
                    navigator = navigator,
                    onLikeClick = { /* Handle like click */ },
                    showModal = { showModal(share) },
                    scope = rememberCoroutineScope(),
                    fetchCommentIdForDeleteOrUpdate = { comment->
                        fetchCommentIdForDeleteOrUpdate(comment)
                    },
                    showModalForCommentSettings = { showModalForCommentSettings()},
                    showModalForPost = {
                        onShareClick(share)

                    }
                )
                BlackFatDivider()
            }
        }
    }
}








@Composable
fun PostMenuBottomSheetContent(
    modifier: Modifier,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onHideClick: () -> Unit,
    onReportClick: () -> Unit,
    isOwner: Boolean,

) {
    Log.d("PostMenuBottomSheetContent", "Called")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Ορίστε το μέγιστο ύψος για το sheet
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
                    painter = painterResource(id = if(isOwner) R.drawable.update_recipe else R.drawable.hide),
                    contentDescription = "Update Post",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = if(isOwner)"Update Post" else "Hide Post",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { if(isOwner) {onUpdateClick()} else {onHideClick()} }
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
                    painter = painterResource(id = if(isOwner) R.drawable.outline_delete_24 else R.drawable.report),
                    contentDescription = "Delete Post",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = if(isOwner) "Delete Post" else "Report Post",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { if(isOwner) {onDeleteClick()} else {onReportClick()} }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
