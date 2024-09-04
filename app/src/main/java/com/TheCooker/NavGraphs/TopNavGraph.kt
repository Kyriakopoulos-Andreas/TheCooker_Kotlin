package com.TheCooker.NavGraphs

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.TheCooker.Drawer.Calendar.Calendar
import com.TheCooker.Drawer.DrawerScreens
import com.TheCooker.Drawer.Help.Help
import com.TheCooker.Drawer.Informations.Information
import com.TheCooker.Drawer.Options.Options
import com.TheCooker.Login.Authentication.GoogleAuth.GoogleClient
import com.TheCooker.Login.LoginViewModel
import com.TheCooker.Menu.MenuView
import com.example.cooker.ChatView.ChatView
import com.example.cooker.HomeView.HomeView
import com.TheCooker.Login.SignIn.UserData

import com.TheCooker.Profile.ProfileView
import com.TheCooker.SearchToolBar.ApiService.MealDetail
import com.TheCooker.SearchToolBar.ApiService.MealsCategory
import com.TheCooker.SearchToolBar.ViewModels.MealsDetailViewModel
import com.TheCooker.SearchToolBar.ViewModels.MealsViewModel
import com.TheCooker.SearchToolBar.ViewModels.SearchCategoryViewModel
import com.TheCooker.SearchToolBar.Views.CreateMeal
import com.TheCooker.SearchToolBar.Views.MealDetailView
import com.TheCooker.SearchToolBar.Views.MealsView
import com.TheCooker.SearchToolBar.Views.SearchView
import dagger.hilt.android.AndroidEntryPoint


@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun TopNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    user: UserData?,
    client: GoogleClient,
    navLogin: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    topBarRoute: MutableState<Boolean>
) {
    val recipeViewModel: SearchCategoryViewModel = hiltViewModel()
    val recipeState by recipeViewModel.categoriesState
    val mealsViewModel: MealsViewModel = hiltViewModel()
    val mealState by mealsViewModel.mealState.observeAsState(MealsViewModel.MealsState())

    val detailViewModel: MealsDetailViewModel = hiltViewModel()

    val detailState by detailViewModel.mealsDetailState.observeAsState(initial = MealsDetailViewModel.MealsDetailState())

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .background(color = Color(0xFF292929))
    ) {

        NavHost(navController = navController as NavHostController, startDestination = TopBarMenu.HomeView.route) {
            composable(route = "MenuView") {
                MenuView(user, googleClient = client, navLogin, loginViewModel)
            }
            composable(DrawerScreens.drawerScreensList[0].route){
                Calendar(topBarRoute = topBarRoute)
            }

            composable(DrawerScreens.drawerScreensList[1].route){
                Options(topBarRoute = topBarRoute)
            }

            composable(DrawerScreens.drawerScreensList[2].route) {
                Information(topBarRoute = topBarRoute)
            }

            composable(DrawerScreens.drawerScreensList[3].route) {
                Help(topBarRoute = topBarRoute)
            }

            composable(route = "CreateMeal"){
                CreateMeal()
            }



            composable(route = TopBarMenu.SearchView.route) {
                var shouldNavigate by remember { mutableStateOf(false) }

                SearchView(
                    recipeState = recipeState,
                    navigateToMeals = { category ->
                        mealsViewModel.fetchMeals(category.strCategory)
                        shouldNavigate = true // Θέτουμε το flag ότι θα γίνει πλοήγηση
                    },
                    fetchMeals = { categoryName ->
                        mealsViewModel.fetchMeals(categoryName)
                    }
                )

                // Εάν πρέπει να γίνει πλοήγηση και η λίστα των γευμάτων δεν είναι κενή
                LaunchedEffect(mealState.list) {
                    if (shouldNavigate && mealState.list.isNotEmpty()) {
                        // Αποθήκευση της λίστας των γευμάτων στο savedStateHandle του currentBackStackEntry
                        navController.currentBackStackEntry?.savedStateHandle?.set("meals", mealState.list)
                        // Πλοήγηση στην οθόνη MealsView
                        navController.navigate("MealsView")
                        shouldNavigate = false // Επαναφορά του shouldNavigate σε false

                    }
                }
            }

            composable(route = "MealsView") {
                // Ανάκτηση της λίστας των γευμάτων από το savedStateHandle του προηγούμενου BackStackEntry
                val meals = navController.previousBackStackEntry?.savedStateHandle?.get<List<MealsCategory>>("meals") ?: emptyList()
                var shouldNavigate by remember { mutableStateOf(false) }

                MealsView(
                    mealsState = MealsViewModel.MealsState(),
                    meals = meals,
                    navigateToDetails = { meal ->
                        detailViewModel.fetchDetails(meal.strMeal)
                        shouldNavigate = true
                    },
                    fetchDetails = { mealName ->
                        mealsViewModel.fetchMeals(mealName)
                    },
                    navController = navController

                )
                LaunchedEffect(detailState.list){
                    if (shouldNavigate && detailViewModel.mealsDetailState.value!!.list.isNotEmpty()) {
                        navController.currentBackStackEntry?.savedStateHandle?.set("detail", detailViewModel.mealsDetailState.value!!.list)
                        navController.navigate("MealDetailView")
                        shouldNavigate = false
                    }

                }

            }

            composable(route = "MealDetailView") {
                val detail = navController.previousBackStackEntry?.savedStateHandle?.get<List<MealDetail>>("detail") ?: emptyList()
                MealDetailView(
                    detailViewModel = detailViewModel,
                    details = detail
                )
            }

            composable(route = TopBarMenu.HomeView.route) {
                HomeView()
            }

            composable(route = TopBarMenu.ProfileView.route) {
                ProfileView(userData = user)
            }
            composable(route = TopBarMenu.ChatView.route) {
                ChatView()
            }
        }
    }
}