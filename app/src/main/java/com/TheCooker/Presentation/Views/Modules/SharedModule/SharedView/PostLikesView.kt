package com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel

@Composable
fun PostLikesView(
    homeViewModel: HomeViewModel?,
    profileViewModel: ProfileViewModel?,
    share: UserMealDetailModel?
) {
    val viewModel = homeViewModel ?: profileViewModel
    val likedUsers = viewModel?.likedUsers?.collectAsState()
    val loading = viewModel?.loadingState?.collectAsState() ?: remember { mutableStateOf(false) }


    val listState = rememberLazyListState()
    LaunchedEffect(share?.recipeId) {
        viewModel?.resetLikedUsers()// καθαρίζεις τα παλιά likes
        if (share != null) {
            viewModel?.loadUsersWithPagination(null, share)
        }
    }

// Αρχικό fetch των χρηστών όταν το σχόλιο αλλάξει ή το composable εμφανιστεί
    LaunchedEffect(share) {
        // Ελέγχουμε αν το σχόλιο δεν είναι null
        if (share != null) {
            // Ελέγχουμε αν το viewModel είναι διαθέσιμο
            if (viewModel != null) {
                // Καλούμε τη συνάρτηση για να φορτώσουμε τους χρήστες με pagination
                viewModel.loadUsersWithPagination(null, share)
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
                    if (!loading.value && share != null) {
                        // Ελέγχουμε αν το viewModel είναι διαθέσιμο
                        if (viewModel != null) {
                            // Καλούμε τη συνάρτηση για να φορτώσουμε περισσότερους χρήστες
                            viewModel.loadUsersWithPagination(null, share)
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



