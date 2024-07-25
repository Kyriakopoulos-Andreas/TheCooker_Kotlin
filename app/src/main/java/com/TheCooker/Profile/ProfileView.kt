package com.TheCooker.Profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.TheCooker.Login.Authentication.UserData


@Composable
fun ProfileView(userData: UserData?) {
    if (userData == null) {
        // Εδώ μπορείς να εμφανίσεις ένα μήνυμα ή να προχωρήσεις με μια fallback UI
        Text(
            text = "Loading profile...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
        )
    } else {
        // Τα δεδομένα υπάρχουν, προχωράμε με την εμφάνιση των στοιχείων του προφίλ
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Έλεγχος αν το URL είναι null ή όχι
                if (userData.profilerPictureUrl != null) {
                    AsyncImage(
                        model = userData.profilerPictureUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("No image")

                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Εμφάνιση άλλων στοιχείων του χρήστη
            Text(
                text = "Username: ${userData.userName}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
