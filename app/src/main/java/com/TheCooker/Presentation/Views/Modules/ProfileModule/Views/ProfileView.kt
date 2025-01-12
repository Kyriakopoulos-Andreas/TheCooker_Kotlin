package com.TheCooker.Presentation.Views.Modules.ProfileModule.Views

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.ProfileModels.ProfileModel
import com.TheCooker.Presentation.Views.Modules.Dividers.BlackFatDivider
import com.TheCooker.Presentation.Views.Modules.Dividers.ThinYellowDivider
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.R
import kotlinx.coroutines.launch


@SuppressLint("ResourceAsColor")
@Composable
fun ProfileView(userData: UserDataModel?) {
    var imageUriProfile by remember { mutableStateOf<Uri?>(null) }
    var imageUriBackground by remember { mutableStateOf<Uri?>(null) }
    val profileViewModel: ProfileViewModel = hiltViewModel()



    Log.d("PhotoIn", profileViewModel._userDataProvider.userData?.profilePictureUrl.toString())
    val context = LocalContext.current


    val launcherProfilePic = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.viewModelScope.launch {
                val result = profileViewModel.updatePhoto(it, "profile")
                when (result) {
                    is uploadDownloadResource.Success -> {
                        Log.d("Upload Image", "Success")
                        imageUriProfile = it
                        profileViewModel._userDataProvider.userData?.profilePictureUrl
                    }

                    is uploadDownloadResource.Error -> {
                        Toast.makeText(context, "Something goes wrong with uploading", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    val launcherBackgroundPic = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.viewModelScope.launch {
                val result = profileViewModel.updatePhoto(it, "background")
                when (result) {
                    is uploadDownloadResource.Success -> {
                        Log.d("Upload Image", "Success")
                        imageUriBackground = it
                        profileViewModel._userDataProvider.userData?.backGroundPictureUrl
                    }
                    is uploadDownloadResource.Error -> {
                        Toast.makeText(context, "Something goes wrong with uploading", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }






    var userProfilePicExists by remember { mutableStateOf(false) }
    var userBackgroundPicExists by remember { mutableStateOf(false) }

   if (profileViewModel._userDataProvider.userData?.profilePictureUrl != null) {
       userProfilePicExists = true}

    if (profileViewModel._userDataProvider.userData?.backGroundPictureUrl != null) {
        userBackgroundPicExists = true}

    var selectedItem by remember {
        mutableIntStateOf(0)
    }





    val painterProfileIm = when {
        imageUriProfile != null -> imageUriProfile
        userProfilePicExists -> profileViewModel._userDataProvider.userData?.profilePictureUrl.toString()
        else -> R.drawable.tt
    }

    val painterBackgroundIm = when {
        imageUriBackground != null -> imageUriBackground
        userBackgroundPicExists -> profileViewModel._userDataProvider.userData?.backGroundPictureUrl.toString()
        else -> R.drawable.profile_background
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(0.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(0.dp),
                contentAlignment = Alignment.TopStart
            ) {
                AsyncImage(
                    model = painterBackgroundIm,
                    contentDescription = "Add Meal Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if(!isInternetAvailable(context =context )){
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            launcherBackgroundPic.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .offset(
                            x = (-9).dp,
                            y = (-9).dp
                        )
                        .border(
                            width = 1.dp,
                            color = Color(R.color.ProfileCameraAssetBackground),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.darkGrey))
                        .clickable {
                            if(!isInternetAvailable(context =context )){
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            launcherProfilePic.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                        contentDescription = "Change Profile Image",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }



                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = 16.dp, y = 120.dp) // Ρύθμιση για τη θέση της εικόνας
                        .clip(CircleShape)  // Εξασφαλίζει ότι η εικόνα είναι κυκλική
                        .border(
                            width = 3.dp,
                            color = Color(0xFF292929),  // Σταθερό χρώμα για το περίγραμμα
                            shape = CircleShape  // Κυκλικό περίγραμμα
                        )
                ) {
                    // Εικόνα
                    AsyncImage(
                        model = painterProfileIm,
                        contentDescription = "ProfilePicture",
                        modifier = Modifier.fillMaxSize().clickable {  launcherProfilePic.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )

                    Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .offset(
                            x = (-9).dp,
                            y = (-9).dp
                        )
                        .border(
                            width = 1.dp,
                            color = Color(R.color.ProfileCameraAssetBackground),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(colorResource(id = R.color.darkGrey)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                            contentDescription = "Change Profile Image",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp)) // Adjust spacing as needed

            Row(modifier = Modifier.padding(start = 12.dp)) {
                profileViewModel._userDataProvider.userData?.userName.let {
                    if (it != null) {
                        Text(
                            text = it,
                            style = androidx.compose.ui.text.TextStyle(color = Color.White),
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .padding(start = 64.dp, end = 64.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { profileViewModel.setProfileManagement(edit = true)
                        profileViewModel.setInformation(false)
                              },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = colorResource(id = R.color.yellow)
                    )
                ) {
                    Text(
                        text = "Edit Profile",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            BlackFatDivider()
            Spacer(modifier = Modifier.height(8.dp))

            NavigationBar(
                tonalElevation = 4.dp,
                containerColor = Color.Transparent,
                modifier = Modifier.height(32.dp),
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileModel.itemsList.forEachIndexed { index, choose ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                Log.d("index", index.toString())
                                if (index == 1) {
                                    profileViewModel.fetchedInfoFromFirebase()
                                    profileViewModel.setInformation(true)
                                    profileViewModel.setProfileManagement(edit = false)

                                }
                                else{
                                    profileViewModel.setInformation(false)
                                }
                                if (index == 0) {
                                    profileViewModel.setProfileManagement(edit = false)
                                }

                            },
                            icon = {
                                Text(
                                    text = choose.title,
                                    color = colorResource(id = R.color.yellow),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = colorResource(id = R.color.profileView_menu_item_selected)
                            )
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ThinYellowDivider()

            if (profileViewModel.information.value) {
                Log.d("Join: ", "in")
                ProfileInformationView(profileViewModel)
            }
            if(profileViewModel.editProfile.value){
                selectedItem = 1
                ProfileInformationView(profileViewModel)

            }

    }
}

