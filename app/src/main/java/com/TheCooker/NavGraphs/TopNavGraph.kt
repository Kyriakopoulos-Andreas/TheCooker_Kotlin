package com.TheCooker.NavGraphs

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.TheCooker.SearchToolBar.RecipeRepo.MealDetail
import com.TheCooker.SearchToolBar.ApiService.UserRecipe
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategory
import com.TheCooker.SearchToolBar.ViewModels.CreateMealViewModel

import com.TheCooker.SearchToolBar.ViewModels.MealsDetailViewModel
import com.TheCooker.SearchToolBar.ViewModels.MealsViewModel
import com.TheCooker.SearchToolBar.ViewModels.SearchCategoryViewModel
import com.TheCooker.SearchToolBar.Views.CreateMeal
import com.TheCooker.SearchToolBar.Views.MealDetailView
import com.TheCooker.SearchToolBar.Views.MealsView
import com.TheCooker.SearchToolBar.Views.SearchView
import kotlinx.coroutines.launch


@SuppressLint("MutableCollectionMutableState")
@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun TopNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    user: UserData?,
    client: GoogleClient,
    navLogin: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    topBarRoute: MutableState<Boolean>,
    createMealViewModel: CreateMealViewModel
) {
    val recipeViewModel: SearchCategoryViewModel = hiltViewModel()
    val recipeState by recipeViewModel.categoriesState.collectAsState()
    val mealsViewModel: MealsViewModel = hiltViewModel()
    val mealState by mealsViewModel.mealState.observeAsState(MealsViewModel.ApiMealsState())
    val userMealState by mealsViewModel.userMealState.observeAsState(MealsViewModel.UserMealsState())
    val loading by mealsViewModel.loading.observeAsState(false)

    val userRecipeState by createMealViewModel.saveState.observeAsState()

    val combinedMeals by mealsViewModel.combinedMeals.observeAsState(mutableListOf())

    val detailViewModel: MealsDetailViewModel = hiltViewModel()

    val detailState by detailViewModel.mealsDetailState.observeAsState(initial = MealsDetailViewModel.MealsDetailState())


    val scope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .padding(paddingValues)
            .background(color = Color(0xFF292929))
    ) {

        NavHost(
            navController = navController as NavHostController,
            startDestination = TopBarMenu.HomeView.route
        ) {
            composable(route = "MenuView") {
                MenuView(user, googleClient = client, navLogin, loginViewModel, createMealViewModel)
            }
            composable(DrawerScreens.drawerScreensList[0].route) {
                Calendar(topBarRoute = topBarRoute)
            }
            composable(DrawerScreens.drawerScreensList[1].route) {
                Options(topBarRoute = topBarRoute)
            }
            composable(DrawerScreens.drawerScreensList[2].route) {
                Information(topBarRoute = topBarRoute)
            }
            composable(DrawerScreens.drawerScreensList[3].route) {
                Help(topBarRoute = topBarRoute)
            }
            composable(route = "CreateMeal") {

                val categoryId =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>("categoryId")
                Log.d("MealsBeforeCreate", combinedMeals.toString())

                CreateMeal(
                    categoryId = categoryId ?: "",
                    saveNavigateBack = navController::popBackStack,
                    navController = navController,
                    combineMeals = combinedMeals
                )


            }






            composable(route = TopBarMenu.SearchView.route) {
                var shouldNavigate by remember { mutableStateOf(false) }
                val newRecipe =
                    navController.currentBackStackEntry?.savedStateHandle?.get<UserRecipe>("newRecipe")

                SearchView(
                    recipeState = recipeState,
                    navigateToMeals = { category ->
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "categoryId",
                            category.idCategory

                        )
                        scope.launch {
                            category.idCategory?.let { it1 ->
                                mealsViewModel.fetchMeals(
                                    category.strCategory ?: "",
                                    it1
                                )
                            }
                            shouldNavigate = true
                        }
                    },
                    fetchMeals = { categoryName ->
                        val categoryId =
                            navController.currentBackStackEntry?.savedStateHandle?.get<String>("categoryId")
                                ?: ""
                        scope.launch {
                            if (categoryId.isNotEmpty()) {
                                scope.launch {
                                    mealsViewModel.fetchMeals(categoryName, categoryId = categoryId)
                                }
                            }

                        }
                    },
                    mealsViewModel = mealsViewModel
                )

                LaunchedEffect(mealState, userMealState, shouldNavigate, newRecipe, combinedMeals) {
                    if (newRecipe != null) {
                        mealsViewModel.addRecipe(newRecipe, combinedMeals) // Προσθήκη της νέας συνταγής στο ViewModel
                        navController.currentBackStackEntry?.savedStateHandle?.remove<UserRecipe>("newRecipe")
                    }
                    if (shouldNavigate && !mealState.loading && !userMealState.loading && !loading ) {
                        if (mealState.list.isNotEmpty()) {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "meals",
                                mealState.list
                            )

                            navController.navigate("MealsView")
                            shouldNavigate = false
                        } else {
                            // Αν δεν υπάρχουν δεδομένα από τον χρήστη και το API, πλοηγηθείτε παρόλα αυτά

                        }
                    }
                }
            }




            composable(route = "MealsView") {
                // Ανάκτηση της λίστας των γευμάτων από το savedStateHandle του προηγούμενου BackStackEntry
                val meals = navController.previousBackStackEntry?.savedStateHandle?.get<List<MealsCategory>>("meals") ?: emptyList()
                var categoryId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("categoryId") ?: ""

                var shouldNavigate by remember { mutableStateOf(false) }

                MealsView(
                    apiMealsState = MealsViewModel.ApiMealsState(list = mealState.list),
                    meals = meals,
                    navigateToDetails = { meal ->
                        detailViewModel.fetchDetails(meal.id?: "")
                        shouldNavigate = true
                    },
                    fetchDetails = { mealName ->
                        Log.d("CategoryIdHost", "Fetching details for meal: $categoryId")
                        scope.launch {  mealsViewModel.fetchMeals(mealName, categoryId = categoryId) }
                        categoryId = ""

                    },
                    createMeal = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("categoryId", categoryId)
                        navController.navigate("CreateMeal")
                    },
                    navController = navController,
                    userMealsState = userMealState,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel

                    )

                LaunchedEffect(detailState.list) {
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
