package com.TheCooker.Presentation.Views.Modules.TopBarViews

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SecondaryTopBarView(
    title: String,
    topBar: TopBarState,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    previousRoute: MutableState<String?>,
    isUserRecipe: Boolean,
    openBottomSheetMealDetailMenu: () -> Unit,
    mealDetail: MealsDetailViewModel,
    mealsViewModel: MealsViewModel,
    sharedViewModel: SharedViewModel
) {
    val backFromUpdate by mealsViewModel.backFromUpdate.collectAsState()

    Log.d("TitleSecondaryTopBar", title)

    BackHandler {
        handleBackPress(
            topBar = topBar,
            navController = navController,
            scope = scope,
            previousRoute = previousRoute.value,
            backFromUpdate = backFromUpdate,
            sharedViewModel = sharedViewModel
        )
    }

    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = Color(0xFF202020),
        modifier = Modifier.height(70.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    handleBackPress(
                        topBar = topBar,
                        navController = navController,
                        scope = scope,
                        previousRoute = previousRoute.value,
                        backFromUpdate = backFromUpdate,
                        sharedViewModel = sharedViewModel
                    )
                }) {
                    Icon(
                        imageVector = Icons.Sharp.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (topBar.currentRoute == TopBarRoute.MealDetailView && isUserRecipe && topBar.previousRoute != TopBarRoute.DrawerProfile) {
                    TextButton(
                        onClick = { /* Share logic */ },
                        modifier = Modifier
                            .background(Color.Transparent)
                            .border(2.dp, Color(0xFFFFC107), RectangleShape)
                            .width(100.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFFFFC107)
                        )
                    ) {
                        Text("Share", color = Color(0xFFFFC107))
                    }

                    IconButton(onClick = openBottomSheetMealDetailMenu) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            }

            if (topBar.currentRoute == TopBarRoute.DrawerHelp
                || topBar.currentRoute == TopBarRoute.DrawerInformation
                || topBar.currentRoute == TopBarRoute.DrawerProfile
                || topBar.currentRoute == TopBarRoute.DrawerCalendar
                || topBar.currentRoute == TopBarRoute.DrawerOptions
                || topBar.currentRoute == TopBarRoute.HomeViewPostCommentLikes
                || topBar.currentRoute == TopBarRoute.PostProfileView
                || topBar.currentRoute == TopBarRoute.CommentUpdateProfile
                || topBar.currentRoute == TopBarRoute.CommentUpdateHomeView
                || topBar.currentRoute == TopBarRoute.UpdatePostOnProfile
                || topBar.currentRoute == TopBarRoute.ProfileViewPostCommentLikes
                || topBar.currentRoute == TopBarRoute.HomeViewPostLikes
                || topBar.currentRoute == TopBarRoute.ProfileViewPostLikes
                ) {
                Text(
                    text = title,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

fun handleBackPress(
    topBar: TopBarState,
    navController: NavHostController,
    scope: CoroutineScope,
    previousRoute: String?,
    backFromUpdate: Boolean,
    sharedViewModel: SharedViewModel?
) {
    scope.launch {
        when (topBar.currentRoute) {
            TopBarRoute.UpdateMealOnSearch -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                sharedViewModel?.setSelectedIndex(2)
                topBar.currentRoute = TopBarRoute.MealDetailView
                navController.popBackStack()
            }
            TopBarRoute.DrawerProfile -> {
                Log.d("TestRoute", topBar.currentRoute.toString())
                topBar.currentRoute = TopBarRoute.Home

                previousRoute?.let { route ->
                    Log.d("PreviousRoute", "Previous route: $route")
                    when (route) {
                        "SearchView" -> sharedViewModel?.setSelectedIndex(3)
                        "HomeView" -> sharedViewModel?.setSelectedIndex(2)
                        "FriendRequestView" -> sharedViewModel?.setSelectedIndex(1)
                        "NotificationView" -> sharedViewModel?.setSelectedIndex(4)
                    }


                    if (navController.popBackStack(route, inclusive = false)) {
                        Log.d("Navigation", "Navigating back to $route")
                    } else {
                        navController.navigate(route)
                    }
                }
            }


            TopBarRoute.CreateMeal -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                if(topBar.previousRoute == TopBarRoute.MealDetailView){
                    topBar.currentRoute = TopBarRoute.MealDetailView
                    navController.popBackStack()

                }
            }

            TopBarRoute.PostProfileView -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.previousRoute = TopBarRoute.PostProfileView
                topBar.currentRoute = TopBarRoute.DrawerProfile
                navController.popBackStack()
            }

            TopBarRoute.CommentUpdateProfile -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.currentRoute = TopBarRoute.DrawerProfile
                navController.popBackStack()
            }

            TopBarRoute.CommentUpdateHomeView -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.currentRoute = TopBarRoute.Home
                navController.popBackStack()
            }

            TopBarRoute.HomeViewPostCommentLikes -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.currentRoute = TopBarRoute.Home
                sharedViewModel?.setSelectedIndex(2)
                navController.popBackStack()
                Log.d("TestRoute4AfterJump", topBar.currentRoute.toString() )
            }
            TopBarRoute.MealDetailView -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.currentRoute = TopBarRoute.Home
                sharedViewModel?.setSelectedIndex(3)
                navController.popBackStack()
                Log.d("TestRoute4AfterJump", topBar.currentRoute.toString() )
            }

            TopBarRoute.UpdatePostOnProfile -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.currentRoute = TopBarRoute.DrawerProfile
                sharedViewModel?.setSelectedIndex(-1)
                navController.popBackStack()
            }

            TopBarRoute.ProfileViewPostCommentLikes -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.currentRoute = TopBarRoute.DrawerProfile
                sharedViewModel?.setSelectedIndex(-1)
                navController.popBackStack()
            }
            TopBarRoute.HomeViewPostLikes -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                sharedViewModel?.setSelectedIndex(2)
                topBar.currentRoute = TopBarRoute.Home
                navController.popBackStack()
            }
            TopBarRoute.ProfileViewPostLikes -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                topBar.previousRoute = TopBarRoute.ProfileViewPostLikes
                sharedViewModel?.setSelectedIndex(-1)
                topBar.currentRoute = TopBarRoute.DrawerProfile
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("returning_from", "likes")

                navController.popBackStack()
            }
            TopBarRoute.HomeViewPostView, -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                sharedViewModel?.setSelectedIndex(2)
                topBar.currentRoute = TopBarRoute.Home
                navController.popBackStack()
            }

            TopBarRoute.JoinOnProfileFromFriendRequestView -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                sharedViewModel?.setSelectedIndex(1)
                topBar.currentRoute = TopBarRoute.Home
                Log.d("TestRoute4AfterJump", topBar.currentRoute.toString() )
                navController.popBackStack()
            }

            TopBarRoute.JoinOnProfileFromHome -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                sharedViewModel?.setSelectedIndex(2)
                topBar.currentRoute = TopBarRoute.Home
                Log.d("TestRoute4AfterJump", topBar.currentRoute.toString() )
                navController.popBackStack()
            }

            TopBarRoute.FriendRequestView -> {
                Log.d("TestRoute", topBar.currentRoute.toString() )
                sharedViewModel?.setSelectedIndex(2)
                topBar.currentRoute = TopBarRoute.Home
                navController.popBackStack()
            }

            else -> {
                if (backFromUpdate) {
                    Log.d("TestRoute5", topBar.currentRoute.toString() )
                    navController.navigate("SearchView")
                } else {
                    navController.popBackStack()
                }
            }
        }
    }
}



