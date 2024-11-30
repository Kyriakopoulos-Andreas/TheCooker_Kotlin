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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.TheCooker.Menu.topBars

import com.TheCooker.Profile.ProfileView
import com.TheCooker.SearchToolBar.ApiService.UserRecipe
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
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


@SuppressLint("MutableCollectionMutableState", "StateFlowValueCalledInComposition",
    "CoroutineCreationDuringComposition", "SuspiciousIndentation"
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun TopNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    user: UserData?,
    client: GoogleClient,
    navLogin: NavHostController,
    loginViewModel: LoginViewModel ,
    topBars: topBars,
    createMealViewModel: CreateMealViewModel,
    detailViewModel: MealsDetailViewModel,
    mealsViewModel: MealsViewModel,
    searchCategoryViewModel: SearchCategoryViewModel

) {

    val recipeState by searchCategoryViewModel.categoriesState.collectAsState()

    val mealState by mealsViewModel.mealState.observeAsState(MealsViewModel.ApiMealsState())
    val userMealState by mealsViewModel.userMealState.observeAsState(MealsViewModel.UserMealsState())
    val loading by mealsViewModel.loading.observeAsState(false)

    val userRecipeState by createMealViewModel.saveState.observeAsState()


    // Αρχικοποιoυμε το singleton με το mealsViewModel

    val sharedViewModel: TopNavGraphSharedViewModel = viewModel()






    val detailState by detailViewModel.mealsDetailState.observeAsState(initial = MealsDetailViewModel.MealsDetailState())


    val scope = rememberCoroutineScope()


    Box(
        modifier = Modifier
            .padding(paddingValues)
            .background(color = Color(0xFF292929))
    ) {
        val sharedCategoryId by sharedViewModel.categoryId.observeAsState("")
        val combinedMeals by mealsViewModel.combinedMeals.observeAsState(mutableListOf())
        val backFromUpdate by mealsViewModel.backFromUpdate.collectAsState()
        val backFromDeleteFlagForFetch by mealsViewModel.backFromDeleteFlagForFetch.collectAsState()


        NavHost(
            navController = navController as NavHostController,
            startDestination = TopBarMenu.HomeView.route
        ) {

            composable(route = "MenuView") {
                MenuView(user, googleClient = client, navLogin, loginViewModel, createMealViewModel, detailViewModel, mealsViewModel ,searchCategoryViewModel= searchCategoryViewModel)
            }
            composable(DrawerScreens.drawerScreensList[0].route) {
                Calendar(topBar = topBars)
            }
            composable(DrawerScreens.drawerScreensList[1].route) {
                Options(topBar = topBars)
            }
            composable(DrawerScreens.drawerScreensList[2].route) {
                Information(topBar = topBars)
            }
            composable(DrawerScreens.drawerScreensList[3].route) {
                Help(topBar = topBars)
            }
            composable(route = "CreateMeal?recipeId={recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")

                //Fetch ξανα απο την βαση των meals μετα το update γιατι υπηρχε θεμα με το combineMeals
                if(backFromUpdate)
                scope.launch {
                    mealsViewModel.fetchMeals("", sharedCategoryId)

                }



                CreateMeal(
                    mealDetailViewModel = detailViewModel ,
                    recipeId = recipeId,
                    categoryId = sharedCategoryId ?: "",
                    saveNavigateBack = navController::popBackStack,
                    navController = navController,
                    combineMeals = combinedMeals,
                    topBars = topBars,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel,
                )


            }






            composable(route = TopBarMenu.SearchView.route) {
                var shouldNavigate by remember { mutableStateOf(false) }

                val newRecipe =
                    navController.currentBackStackEntry?.savedStateHandle?.get<UserRecipe>("newRecipe")

                val updatedMeal =
                    navController.currentBackStackEntry?.savedStateHandle?.get<UserRecipe>("updatedRecipe")



                SearchView(
                    recipeState = recipeState,
                    topNavGraphSharedViewModel = sharedViewModel,
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
                        mealsViewModel.addRecipe(
                            newRecipe,
                            combinedMeals
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.remove<UserRecipe>("newRecipe")
                    }
                    if (shouldNavigate && !mealState.loading && !userMealState.loading && !loading) {

                        if (mealState.list.isNotEmpty()) {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "meals",
                                mealState.list
                            )

                            navController.navigate("MealsView")
                            shouldNavigate = false
                        }
                    }
                }

                LaunchedEffect(key1 = backFromUpdate) {
                    if (backFromUpdate) {

                        if (mealState.list.isNotEmpty()) {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "mealsAfterUpdate",
                                mealState.list
                            )
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "CatId",
                                updatedMeal?.categoryId.toString()

                            )
                            mealsViewModel.setBackFromUpdate(false)
                            mealsViewModel.setBackFromDeleteFlagForFetch(false)
                            navController.navigate("MealsView")

                        }
                    }
                }

                LaunchedEffect(key1 = backFromDeleteFlagForFetch) {
                    Log.d("BackFromDeleteFlag", backFromDeleteFlagForFetch.toString())

                    if (backFromDeleteFlagForFetch) {
                        scope.launch {
                            mealsViewModel.fetchMeals("", sharedCategoryId)
                            mealsViewModel.setBackFromDeleteFlagForFetch(false)
                            if (mealState.list.isNotEmpty()) {
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "mealsAfterUpdate",
                                    mealState.list
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "CatId",
                                    updatedMeal?.categoryId.toString()

                                )

                                navController.navigate("MealsView")
                                mealsViewModel.setBackFromUpdate(false)
                                }
                            }
                        }
                    }
            }








            composable(route = "MealsView") {
                // Ανάκτηση των Data απο την στοιβα του navController
                val mealsBeforeUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<List<MealsCategory>>("meals") ?: emptyList()
                var categoryId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("categoryId") ?: ""

                val updatedMeal = navController.currentBackStackEntry?.savedStateHandle?.get<UserRecipe>("updatedRecipe")
                if (updatedMeal != null) {
                    mealsViewModel.updateRecipeOnLiveList(updatedMeal, mealsBeforeUpdate as MutableList<MealItem>)
                }
                val mealsAfterUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<List<MealsCategory>>("mealsAfterUpdate") ?: emptyList()
                val meals = if(mealsAfterUpdate.isNotEmpty()){mealsAfterUpdate}else{mealsBeforeUpdate}








                var shouldNavigate by remember { mutableStateOf(false) }

                MealsView(
                    apiMealsState = MealsViewModel.ApiMealsState(list = mealState.list),
                    meals = meals,
                    navigateToDetails = { meal ->
                        detailViewModel.fetchDetails(meal.id?: "")
                        shouldNavigate = true
                    },
                    fetchDetails = { mealName ->
                        Log.d("CategoryIdHost", "Fetching details for meal: $mealName")
                        scope.launch {

                                mealsViewModel.fetchMeals(mealName, categoryId = categoryId)

                        }
                        categoryId = ""

                    },
                    createMeal = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("categoryId", categoryId)
                        navController.navigate("CreateMeal")
                    },
                    navController = navController,
                    userMealsState = userMealState,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel,
                    topBar = topBars

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
                val navBackStackEntry = navController.currentBackStackEntry
                val detail = navBackStackEntry?.savedStateHandle?.get<UserRecipe>("updatedMeal")

                navController.previousBackStackEntry?.savedStateHandle?.set("updatedRecipe", detail)

                Log.d("DetailOnNav", detail.toString())
                MealDetailView(
                    detailViewModel = detailViewModel,
                    updateDetails = detail
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
