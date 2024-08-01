package com.TheCooker.Profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.TheCooker.Login.Authentication.GoogleAuth.SignInState
import com.TheCooker.Login.Authentication.GoogleAuth.UserData
import com.TheCooker.Login.CrPassword.User
import com.TheCooker.Login.LoginViewModel
import com.TheCooker.R


@Composable
fun ProfileView(userData: UserData?, loginUser: User?) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    println(loginUser?.lastName)




    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            userData?.profilerPictureUrl = it.toString()
        }
    }

    var isGoogleSignIn by remember { mutableStateOf(false) }

   if (userData?.profilerPictureUrl != null) isGoogleSignIn = true

    val defaultProfileImage = painterResource(id = R.drawable.logo)
    // Εμφανίστε την εικόνα του χρήστη αν υπάρχει, αλλιώς εμφάνιση της εικόνας του Google ή της προεπιλεγμένης εικόνας
    val painter = when {
        isGoogleSignIn -> userData?.profilerPictureUrl
        else -> R.drawable.logo
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.padding(end = 30.dp))
        if (userData != null) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(140.dp)
                    .clip(shape = CircleShape)
            ) {
                AsyncImage(
                    model = painter,
                    contentDescription = "ProfilePicture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-12).dp, y = (-16).dp) // Adjust the offset to fit inside the circle
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Change Profile Image",
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            userData.userName?.let {
                Text(
                    text = it,
                    style = androidx.compose.ui.text.TextStyle(color = Color.White),
                    fontSize = 24.sp
                )
            }
        }
    }
}

