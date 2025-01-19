package com.TheCooker.Presentation.Views.Modules.ProfileModule.Views

import android.graphics.Bitmap
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarsModel
import com.TheCooker.Presentation.Views.Modules.Dividers.BlackFatDivider
import com.TheCooker.Presentation.Views.Modules.Dividers.ThinYellowDivider
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.StepBottomSheetContent
import com.TheCooker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SharesView(
    profileViewModel: ProfileViewModel,
    showModal: (UserMealDetailModel?) -> Unit,
    hideModal: () -> Unit,
    navigator: NavController,
    topBarManager: TopBarsModel

) {

    var selectedShare by remember { mutableStateOf<UserMealDetailModel?>(null) }

    LaunchedEffect(Unit) {
        profileViewModel.fetchShares()

    }


    LaunchedEffect(selectedShare) {
        if (selectedShare != null) {
        showModal(selectedShare)
        } else {
            hideModal()
        }
    }


    val shares by profileViewModel.shares.collectAsState()




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
                ShareItem(
                    share = share,
                    userName = profileViewModel._userDataProvider.userData?.userName ?: "Unknown",
                    imageUri = profileViewModel._userDataProvider.userData?.profilePictureUrl,
                    profileViewModel = profileViewModel,
                    navigator = navigator,
                    topBarManager = topBarManager
                ) {
                    selectedShare = share
                    showModal(share)
                }
                BlackFatDivider()
            }
        }
    }
}





@Composable
fun ShareItem(
    share: UserMealDetailModel,
    userName: String,
    imageUri: String? = null,
    profileViewModel: ProfileViewModel,
    navigator: NavController,
    topBarManager: TopBarsModel,
    showModal: () -> Unit,



) {


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

            Row(verticalAlignment = Alignment.CenterVertically,
                ) {
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

                Column(
                    verticalArrangement = Arrangement.Center
                ) {
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
            IconButton(
                onClick = { showModal()},
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_post),
                    contentDescription = "Menu Icon",
                    tint = colorResource(id = R.color.yellow)
                )
            }
        }



            // Εικόνα του share!
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        ) {
            AsyncImage(
                model = share.recipeImage,
                contentDescription = "Meal Image",
                modifier = Modifier
                    .fillMaxSize()
                    .height(180.dp)
                    .clickable {
                        Log.d("ShareOnClick", share.toString())
                        navigator.currentBackStackEntry?.savedStateHandle?.set("PostRecipe", share)
                        topBarManager.postBarRoute = true
                        topBarManager.menuTopBarRoute = false
                        topBarManager.drawerMenuRoute = false
                        topBarManager.mealTopBarRoute = false
                        topBarManager.updateBar = false

                        navigator.navigate("MealDetailView")

                    },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            // Όνομα συνταγής
            Text(
                text = share.recipeName ?: "No Recipe Name",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White // Λευκό κείμενο
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        ThinYellowDivider()

        // Footer: Like, Comment, Share
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionButton(icon = R.drawable.home1, label = "Like")
            ActionButton(icon = R.drawable.rounded_comment_24, label = "Comment")
            ActionButton(icon = R.drawable.baseline_share_24, label = "Share")
        }
    }
}

@Composable
fun ActionButton(icon: Int, label: String) {
    TextButton(
        onClick = { /* Action */ },
        colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(18.dp),
            tint = colorResource(id = R.color.yellow),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = TextStyle(fontSize = 14.sp, color = colorResource(id = R.color.yellow), fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun PostMenuBottomSheetContent(
    modifier: Modifier,
    onDeleteClick: () -> Unit,
    onUpdateClick: () -> Unit
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
                    painter = painterResource(id = R.drawable.update_recipe),
                    contentDescription = "Update Post",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Update Post",
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
                    contentDescription = "Delete Post",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Delete Post",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onDeleteClick() }
                    )
                }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
