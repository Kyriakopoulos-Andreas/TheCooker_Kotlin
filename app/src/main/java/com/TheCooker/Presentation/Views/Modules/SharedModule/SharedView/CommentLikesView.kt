package com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel

@Composable
fun CommentLikesView(
    homeViewModel: HomeViewModel?,
    profileViewModel: ProfileViewModel?,
    comment: PostCommentModel?,
    share: UserMealDetailModel?
) {
    val viewModel = homeViewModel ?: profileViewModel
    val likedUsers = viewModel?.likedUsers?.collectAsState()
    val loading = viewModel?.loadingState?.collectAsState() ?: remember { mutableStateOf(false) }


    val listState = rememberLazyListState()
    LaunchedEffect(comment?.commentId) {
        viewModel?.resetLikedUsers()// καθαρίζεις τα παλιά likes
        if (comment != null) {
            viewModel?.loadUsersWithPagination(comment, null)
        }
    }

// Αρχικό fetch των χρηστών όταν το σχόλιο αλλάξει ή το composable εμφανιστεί
    LaunchedEffect(comment) {
        // Ελέγχουμε αν το σχόλιο δεν είναι null
        if (comment != null) {
            // Ελέγχουμε αν το viewModel είναι διαθέσιμο
            if (viewModel != null) {
                // Καλούμε τη συνάρτηση για να φορτώσουμε τους χρήστες με pagination
                viewModel.loadUsersWithPagination(comment, null)
            }
        }
    }

// Ανίχνευση scroll για να δούμε αν ο χρήστης έχει φτάσει στο κάτω μέρος της λίστας
    LaunchedEffect(listState) {
        // Χρησιμοποιούμε το snapshotFlow για να παρακολουθούμε την τελευταία ορατή θέση στην lazy list
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                // Παίρνουμε τον συνολικό αριθμό των στοιχείων στην λίστα
                val totalItems = listState.layoutInfo.totalItemsCount

                // Αν η τελευταία ορατή θέση είναι το τελευταίο στοιχείο της λίστας
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItems - 1) {
                    // Αν δεν γίνεται ήδη φόρτωση και το σχόλιο δεν είναι null
                    if (!loading.value && comment != null) {
                        // Ελέγχουμε αν το viewModel είναι διαθέσιμο
                        if (viewModel != null) {
                            // Καλούμε τη συνάρτηση για να φορτώσουμε περισσότερους χρήστες
                            viewModel.loadUsersWithPagination(comment, null)
                        }
                    }
                }
            }
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        likedUsers?.value?.let { users ->
            items(users.size) { index ->
                LikeItem(user = users[index])
            }
        }

        // Loading indicator at bottom
        if (loading.value) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LikeItem(user: UserDataModel) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(68.dp) // Μεγαλύτερη εικόνα
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp)) // Λίγο μεγαλύτερο κενό ανάμεσα στην εικόνα και το όνομα

            Text(
                text = user.userName.toString(),
                color = Color.White,
                style  = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
            )
        }
    }
}

