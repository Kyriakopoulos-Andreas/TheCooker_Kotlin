package com.example.cooker.HomeView
import androidx.compose.foundation.lazy.items
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.Dividers.BlackFatDivider
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView.CommentSettingsBottomSheetContent
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.Post
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    navController: NavController,
    viewModel: HomeViewModel,
    profileViewModel: ProfileViewModel
){

    val scope = rememberCoroutineScope()
    val randomShares = viewModel.randomShares.collectAsStateWithLifecycle()
    val isRefreshing = remember { mutableStateOf(false) }
    Log.d("HomeView", "Recompose")
    val context = LocalContext.current

    var selectedShare by remember { mutableStateOf<UserMealDetailModel?>(null) }



    Log.d("randomShares", randomShares.value.toString())

    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }

    val commentToBeDeleted by viewModel.commentToBeDeletedOrUpdate

    val refreshScope = rememberCoroutineScope()
    fun refresh() {
        refreshScope.launch {
            isRefreshing.value = true
            viewModel.fetchRandomSharesForHomeView()

            isRefreshing.value = false
        }
    }
    val modalSheetStateComment = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val modalSheetStatePost = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    ModalBottomSheetLayout(
        sheetState = modalSheetStateComment,
        sheetShape = RoundedCornerShape(12.dp),
        sheetContent = {
            CommentSettingsBottomSheetContent( // Code of  PostMenuBottomSheetContent is in SharesView.kt and we use lambda expressions to update modalSheetState
                modifier = Modifier.fillMaxWidth(),
                onDeleteClick = {
                    if(!isInternetAvailable(context)){
                        scope.launch {
                            modalSheetStateComment.hide()
                        }
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                        return@CommentSettingsBottomSheetContent
                    }

                    scope.launch {
                        commentToBeDeleted?.commentId?.let { profileViewModel.deleteComment(it) }
                        modalSheetStateComment.hide()
                    }

                },
                onUpdateClick = {
                    if(!isInternetAvailable(context)){
                        scope.launch {
                            modalSheetStateComment.hide()
                        }
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                        return@CommentSettingsBottomSheetContent
                    }

                    navController.currentBackStackEntry?.savedStateHandle?.set<PostCommentModel?>("comment", viewModel.getCommentToBeDeletedOrUpdated())
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "source",
                        "home"
                    )
                    navController.navigate("CommentUpdateView")
                    scope.launch {
                        modalSheetStateComment.hide()
                    }

                },
                isUserComment = viewModel.checkIfIsUserComment()
            )
        }
    ) {

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isRefreshing.value),
            onRefresh = { refresh() }
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                state = listState
            ) {
                items(
                    items = randomShares.value,
                    key = { share -> share.recipeId ?: share.id ?: UUID.randomUUID().toString() }
                ) { share ->




                    share.creatorData?.userName?.let {

                        Post(
                            share = share,
                            userName = it,
                            imageUri = share.creatorData!!.profilePictureUrl,
                            profileViewModel = profileViewModel,
                            navigator = navController,
                            showModal = {
                                scope.launch {
                                    modalSheetStateComment.show()
                                }
                            },
                            onLikeClick = { /* Handle like click */ },
                            scope = scope,
                            homeViewModel = viewModel,
                            fetchCommentForUpdateOrDelete = { comment ->
                                scope.launch {
                                    viewModel.setCommentToBeDeletedOrUpdated(comment)

                                }
                            },
                            showModalForCommentSettings = {
                                scope.launch {
                                    modalSheetStateComment.show()
                                }
                            },
                            showModalForPost = { share ->
                                scope.launch {
                                    selectedShare = share
                                    modalSheetStatePost.show()
                                }

                            }

                        )
                    }
                    BlackFatDivider()
                }
            }
        }
    }
    ModalBottomSheetLayout(
        sheetState = modalSheetStatePost,
        sheetShape = RoundedCornerShape(12.dp),
        sheetContent = {
            PostHomeViewBottomSheetContent(
                modifier = Modifier.fillMaxWidth(),
                onHideClick = {
                    if(!isInternetAvailable(context = context)){
                        profileViewModel.setDeletePostResult("No internet connection")
                    }

                },
                onReportClick = {
                    scope.launch {
                        if(!isInternetAvailable(context = context)){
                            profileViewModel.setDeletePostResult("No internet connection")
                        }
                        modalSheetStatePost.hide()
                    }
                }
            )
        }
    ) {
    }
}

@Composable
fun PostHomeViewBottomSheetContent(
    modifier: Modifier,
    onHideClick: () -> Unit,
    onReportClick: () -> Unit
) {
    Log.d("PostMenuBottomSheetContent", "Called")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
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
                    painter = painterResource(id = R.drawable.hide),
                    contentDescription = "Update Post",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Hide Post",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onHideClick() }
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
                    painter = painterResource(id = R.drawable.report),
                    contentDescription = "Delete Post",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Report Post",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onReportClick() }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}





