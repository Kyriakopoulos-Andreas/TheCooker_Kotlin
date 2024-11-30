package com.TheCooker.Menu

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.TheCooker.SearchToolBar.ApiService.UserRecipe
import com.TheCooker.SearchToolBar.RecipeRepo.MealDetail
import com.TheCooker.SearchToolBar.ViewModels.MealsDetailViewModel
import com.TheCooker.SearchToolBar.ViewModels.MealsViewModel
import com.TheCooker.SearchToolBar.Views.BottomSheetMealDetailMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi", "CoroutineCreationDuringComposition")
@Composable
fun ExtraTopBar(
    title: String,
    topBar: topBars,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    previousRoute: MutableState<String?>,
    isUserRecipe: Boolean,
    openBottomSheetMealDetailMenu: () -> Unit,
    mealDetail: MealsDetailViewModel,
    mealsViewModel: MealsViewModel

){
    Log.d("ExtraTopBar", "mealTopBarRoute: ${topBar.mealTopBarRoute}, updateBar: ${topBar.updateBar}")


    val backFromUpdate by mealsViewModel.backFromUpdate.collectAsState()
    Log.d("TestBackFromUpdate", backFromUpdate.toString())

    BackHandler {
        if (topBar.menuTopBarRoute) {
            scope.launch {
                topBar.menuTopBarRoute = false
                scaffoldState.drawerState.open()
                previousRoute.value?.let { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }

                }
            }

        }
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
                    scope.launch {
                        Log.d("ExtraTopBar1", "mealTopBarRoute: ${topBar.mealTopBarRoute}, updateBar: ${topBar.updateBar}")
                        topBar.menuTopBarRoute = true
                        topBar.drawerMenuRoute = false



                        if(topBar.updateBar){
                            topBar.updateBar = false
                            topBar.mealTopBarRoute = true
                            topBar.menuTopBarRoute = false

                            Log.d("ExtraTopBar2", "mealTopBarRoute: ${topBar.mealTopBarRoute}, updateBar: ${topBar.updateBar}")

                        }

                        if (!topBar.mealTopBarRoute && !topBar.updateBar && !topBar.drawerMenuRoute) {
                            topBar.menuTopBarRoute = true
                            Log.d("PreviousRoute", "jOIN")

                            scaffoldState.drawerState.open()

                            previousRoute.value?.let { route ->
                                Log.d("PreviousRoute", "Previous route: $route")
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true

                                }
                            }
                        }
                        else {
                            Log.d("PreviousRoute", "Previous route: ${navController.currentBackStack.value}")
                            if(backFromUpdate){


                                navController.navigate("SearchView")
                            }else{
                                navController.popBackStack()
                            }



                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Sharp.ArrowBack, contentDescription = "Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                if (topBar.mealTopBarRoute && topBar.updateBar == false)
                    if (isUserRecipe) {
                        TextButton(
                            onClick = { /* Κώδικας για τη λειτουργία Share */ },
                            modifier = Modifier
                                .background(Color.Transparent)
                                .border(2.dp, Color(0xFFFFC107), RectangleShape) // Border
                                .width(100.dp)
                                .height(48.dp)
                                .padding(0.dp),
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFFFFC107)
                            ),
                            elevation = null // No elevation
                        ) {
                            Text("Share", color = Color(0xFFFFC107))
                        }
                        IconButton(onClick = {   openBottomSheetMealDetailMenu() },) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More")
                        }


                    }
            }




            Log.d("ExtraTopBar", "Title: ${topBar.drawerMenuRoute}")

            if (topBar.drawerMenuRoute){
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