package com.TheCooker.Common.Layer.NavGraphs

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarMenuModel
import com.TheCooker.Domain.Layer.Models.ScreenModels.DrawerScreensModel
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Options
import com.TheCooker.Domain.Layer.UseCase.GoogleIntents.GoogleClient
import com.TheCooker.Presentation.Views.Modules.LoginModule.ViewModels.LoginViewModel
import com.example.cooker.ChatView.NotificationView
import com.example.cooker.HomeView.HomeView
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarsModel
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Calendar
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Help
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Information
import com.TheCooker.Presentation.Views.Modules.ProfileModule.Views.ProfileView
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealModel
import com.TheCooker.Presentation.Views.Modules.FriendRequestView
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.CreateMeal
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.MealDetailView
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.MealsView
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.SearchView
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel
import com.TheCooker.Presentation.Views.Modules.TopBarViews.MainTopBarViewSupport
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


@SuppressLint("MutableCollectionMutableState", "StateFlowValueCalledInComposition",
    "CoroutineCreationDuringComposition", "SuspiciousIndentation"
)
@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun TopNavGraph(
    navController: NavController,
    paddingValues: PaddingValues,
    user: UserDataModel?,
    client: GoogleClient,
    navLogin: NavHostController,
    loginViewModel: LoginViewModel,
    TopBarsModel: TopBarsModel,
    createMealViewModel: CreateMealViewModel,
    detailViewModel: MealsDetailViewModel,
    mealsViewModel: MealsViewModel,
    categoryViewModel: CategoryViewModel

) {

    val recipeState by categoryViewModel.categoriesState.collectAsState()

    val mealState by mealsViewModel.mealState.observeAsState(MealsViewModel.ApiMealsState())
    val userMealState by mealsViewModel.userMealState.observeAsState(MealsViewModel.UserMealsState())
    val loading by mealsViewModel.loading.observeAsState(false)




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
        val backFromDeleteFlagForFetch by mealsViewModel.backFromDeleteFlagForFetch.collectAsStateWithLifecycle()


        NavHost(
            navController = navController as NavHostController,
            startDestination = TopBarMenuModel.HomeView.route
        ) {

            composable(route = "MenuView") {
                MainTopBarViewSupport(user, googleClient = client, navLogin, loginViewModel, createMealViewModel, detailViewModel, mealsViewModel ,categoryViewModel= categoryViewModel)
            }
            composable(DrawerScreensModel.drawerScreensList[0].route) {
                val postRecipe = navController.previousBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("PostRecipe")
                Log.d("PostRecipeOnProfile", postRecipe.toString())
                navController.previousBackStackEntry?.savedStateHandle?.set("PostRecipe", postRecipe)


                ProfileView(userData = user, navigator = navController, topBarManager = TopBarsModel)
            }
            composable(DrawerScreensModel.drawerScreensList[1].route) {
                Calendar(topBar = TopBarsModel)
            }
            composable(DrawerScreensModel.drawerScreensList[2].route) {
                Options(topBar = TopBarsModel)
            }
            composable(DrawerScreensModel.drawerScreensList[3].route) {
                Information(topBar = TopBarsModel)
            }
            composable(DrawerScreensModel.drawerScreensList[4].route) {
                Help(topBar = TopBarsModel)
            }
            composable(route = "CreateMeal?recipeId={recipeId}") { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId")
                CreateMeal(
                    mealDetailViewModel = detailViewModel ,
                    recipeId = recipeId,
                    categoryId = sharedCategoryId ,
                    saveNavigateBack = navController::popBackStack,
                    navController = navController,
                    combineMeals = combinedMeals,
                    TopBarsModel = TopBarsModel,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel,
                )
                }









            composable(route = TopBarMenuModel.SearchView.route) {
                var shouldNavigate by remember { mutableStateOf(false) }

                val newRecipe =
                    navController.currentBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("newRecipe")



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
                            navController.currentBackStackEntry?.savedStateHandle?.get<String>("categoryId") ?: ""
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
                        navController.currentBackStackEntry?.savedStateHandle?.remove<UserMealDetailModel>("newRecipe")
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

                LaunchedEffect(key1 = backFromDeleteFlagForFetch) {
                    Log.d("BackFromDeleteFlag1", backFromDeleteFlagForFetch.toString())

                    if (backFromDeleteFlagForFetch) {
                        Log.d("BackFromDeleteFlag2", backFromDeleteFlagForFetch.toString())
                        snapshotFlow { mealState.list }
                            // snapshotFlow: Δημιουργεί ένα Flow που παρακολουθεί την τιμή του mealState.list. Κάθε φορά που αλλάζει αυτή η τιμή, το Flow εκπέμπει τη νέα τιμή.
                            .filter { it.isNotEmpty() }
                            .collectLatest { updatedList ->
                                //collectLatest: : Συλλέγει τις τελευταίες τιμές που εκπέμπονται από το Flow και εκτελεί το μπλοκ κώδικα με την ενημερωμένη λίστα (updatedList).
                                //Αν το Flow εκπέμψει μια νέα τιμή πριν ολοκληρωθεί η επεξεργασία της τρέχουσας τιμής, η προηγούμενη επεξεργασία ακυρώνεται και ξεκινά η νέα.
                                Log.d("BackFromDeleteFlag3", backFromDeleteFlagForFetch.toString())
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "mealsAfterUpdate",
                                    updatedList
                                )
                                navController.navigate("MealsView")
                                mealsViewModel.setBackFromDeleteFlagForFetch(false)
                            }
                    }
                }



                LaunchedEffect(key1 = backFromUpdate) {
                    if(backFromUpdate){
                        snapshotFlow { mealState.list }
                            .filter { it.isNotEmpty() }.
                            collectLatest { listAfterDelete ->
                                navController.currentBackStackEntry?.savedStateHandle?.set("mealsAfterUpdate", listAfterDelete)
                                navController.navigate("MealsView")
                                mealsViewModel.setBackFromUpdate(false)
                            }
                    }
                }
            }








            composable(route = "MealsView") {
                // Ανάκτηση των Data απο την στοιβα του navController
                val mealsBeforeUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<List<UserMealModel>>("meals") ?: emptyList()
                val categoryId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("categoryId") ?: ""

                val mealsAfterUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<List<UserMealModel>>("mealsAfterUpdate") ?: emptyList()
                val meals = mealsAfterUpdate.ifEmpty { mealsBeforeUpdate }








                var shouldNavigate by remember { mutableStateOf(false) }

                MealsView(
                    apiMealsState = MealsViewModel.ApiMealsState(list = mealState.list),
                    meals = meals,
                    navigateToDetails = { meal ->
                        detailViewModel.fetchDetails(meal.id?: "")
                        shouldNavigate = true
                    },
                    createMeal = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("categoryId", categoryId)
                        navController.navigate("CreateMeal")
                    },
                    navController = navController,
                    userMealsState = userMealState,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel,
                    topBar = TopBarsModel

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
                val navBackStackEntry = navController.previousBackStackEntry
                var detail = navBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("updatedMeal")


                val postDetail = remember {
                    navBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("PostRecipe")
                }
                Log.d("PostDetail", postDetail.toString())



                if(postDetail != null){
                    detail = postDetail
                    detailViewModel.setDetailsForPost(postDetail)
                }


                navController.previousBackStackEntry?.savedStateHandle?.set("updatedRecipe", detail)

                Log.d("DetailOnNav", detail.toString())
                MealDetailView(
                    detailViewModel = detailViewModel,
                    updateDetails = detail
                )
            }

            composable(route = TopBarMenuModel.HomeView.route) {
                HomeView()
            }

            composable(route = TopBarMenuModel.FriendRequestView.route) {
                FriendRequestView()
            }


            composable(route = TopBarMenuModel.NotificationView.route) {
                NotificationView()
            }
        }
    }
}
