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
import androidx.compose.runtime.MutableState
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
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel

import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Calendar
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Help
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.Information
import com.TheCooker.Presentation.Views.Modules.ProfileModule.Views.ProfileView
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealModel
import com.TheCooker.Presentation.Views.Modules.FriendRequestModule.FriendRequestView
import com.TheCooker.Presentation.Views.Modules.HomeModule.Module.ViewModels.HomeViewModel
import com.TheCooker.Presentation.Views.Modules.ProfileModule.ViewModels.ProfileViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.CreateMeal
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.MealDetailView
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.MealsView
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.SearchView
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView.CommentUpdateView
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView.CommentLikesView
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedView.PostLikesView
import com.TheCooker.Presentation.Views.Modules.TopBarViews.MainTopBarViewSupport
import com.TheCooker.Presentation.Views.Modules.TopBarViews.TopBarRoute
import com.TheCooker.Presentation.Views.Modules.TopBarViews.TopBarState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


@SuppressLint("MutableCollectionMutableState", "StateFlowValueCalledInComposition",
    "CoroutineCreationDuringComposition", "SuspiciousIndentation", "UnrememberedGetBackStackEntry"
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
    createMealViewModel: CreateMealViewModel,
    detailViewModel: MealsDetailViewModel,
    mealsViewModel: MealsViewModel,
    categoryViewModel: CategoryViewModel,
    topBarState: MutableState<TopBarState>

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
                MainTopBarViewSupport(user, googleClient = client, navLogin, loginViewModel, createMealViewModel, detailViewModel, mealsViewModel ,categoryViewModel= categoryViewModel, topBarState )

            }

            composable(DrawerScreensModel.drawerScreensList[1].route) {
                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.DrawerCalendar)
                Calendar()
            }
            composable(DrawerScreensModel.drawerScreensList[2].route) {
                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.DrawerCalendar)
                Options()
            }
            composable(DrawerScreensModel.drawerScreensList[3].route) {
                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.DrawerInformation)
                Information()
            }
            composable(DrawerScreensModel.drawerScreensList[4].route) {
                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.DrawerHelp)
                Help()
            }
            composable(route = "CreateMeal?recipeId={recipeId}") { backStackEntry ->
                var recipeId = backStackEntry.arguments?.getString("recipeId")
                val postUpdate = navController.currentBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("PostRecipe")
                if (recipeId != null) {
                    Log.d("CheckRecipeId", recipeId)

                }

                if(topBarState.value.currentRoute == TopBarRoute.MealView){
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.UpdateMealOnSearch)
                    topBarState.value = topBarState.value.copy(previousRoute = TopBarRoute.MealView)
                }

                if(postUpdate != null){
                    recipeId = null
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.UpdatePostOnProfile)
                    topBarState.value = topBarState.value.copy(previousRoute = TopBarRoute.PostProfileView)
                }


                Log.d("PostUpdate", postUpdate.toString())

                CreateMeal(
                    mealDetailViewModel = detailViewModel ,
                    recipeId = recipeId,
                    categoryId = sharedCategoryId ,
                    saveNavigateBack = {
                        navController.popBackStack()
                    },
                    navController = navController,
                    combineMeals = combinedMeals,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel,
                    postForUpdate = postUpdate,


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


                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.Home)
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
                Log.d("DetailOnNav", detail.toString())


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

                if(topBarState.value.currentRoute == TopBarRoute.DrawerProfile){
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.MealView)
                    topBarState.value = topBarState.value.copy(previousRoute = TopBarRoute.DrawerProfile)
                }

                if(topBarState.value.currentRoute == TopBarRoute.Home){
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.HomeViewPostView)
                    topBarState.value = topBarState.value.copy(previousRoute = TopBarRoute.Home)
                }

                    //ΠΡΕΠΕΙ ΝΑ ΟΡΙΣΕΙΣ ΤΟ ΤΟΠ ΜΠΑΡ ΣΤΑΤΕ ΚΑΙ ΓΙΑ ΤΟ SEARCH VIEW!!!!!!
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.MealView)


                MealDetailView(
                    detailViewModel = detailViewModel,
                    updateDetails = detail
                )
            }

            composable(route = TopBarMenuModel.HomeView.route) { backStackEntry ->
                // Ελέγξτε αν το HomeView είναι ήδη στο back stack
                val homeBackStackEntry = navController.previousBackStackEntry
                    ?.takeIf { it.destination.route == TopBarMenuModel.HomeView.route }

                // Αν υπάρχει, χρησιμοποιούμε το ήδη υπάρχον ViewModel, αλλιώς δημιουργούμε νέο
                val homeViewModel = homeBackStackEntry?.let {
                    hiltViewModel<HomeViewModel>(it)
                } ?: hiltViewModel<HomeViewModel>(backStackEntry)

                // Ελέγξτε αν το ProfileView είναι ήδη στο back stack
                val profileBackStackEntry = navController.previousBackStackEntry
                    ?.takeIf { it.destination.route == "Profile" }

                // Αν υπάρχει, χρησιμοποιούμε το ήδη υπάρχον ProfileViewModel, αλλιώς δημιουργούμε νέο
                val profileViewModel = profileBackStackEntry?.let {
                    hiltViewModel<ProfileViewModel>(it)
                } ?: hiltViewModel<ProfileViewModel>(backStackEntry)

                // Ρύθμιση της κατάστασης της topBar
                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.Home)

                // Παράδοση των ViewModels στο HomeView
                HomeView(
                    navController = navController,
                    viewModel = homeViewModel,
                    profileViewModel = profileViewModel
                )
            }


            composable(route = "Profile") {
                    backStackEntry ->
                val postRecipe = navController.previousBackStackEntry?.savedStateHandle?.get<UserMealDetailModel?>("PostRecipe")
                Log.d("PostRecipeOnProfile", postRecipe.toString())
                navController.currentBackStackEntry?.savedStateHandle?.set("PostRecipe", postRecipe)

                topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.DrawerProfile)
                ProfileView(navigator = navController,)
            }

            composable(route = "PostLikesView") {
                val previousBackStackEntry = navController.previousBackStackEntry

                val source = remember { previousBackStackEntry?.savedStateHandle?.get<String>("source") }
                val post = remember { previousBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("post") }

                LaunchedEffect(Unit) {
                    previousBackStackEntry?.savedStateHandle?.remove<String>("source")
                    previousBackStackEntry?.savedStateHandle?.remove<UserMealDetailModel>("post")
                }

                val homeViewModel = if (source == "home") {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.HomeViewPostLikes)
                    val backStackEntry = navController.getBackStackEntry("HomeView")
                    hiltViewModel<HomeViewModel>(backStackEntry)
                }else null

                val profileViewModel = if (source == "profile") {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.ProfileViewPostLikes)
                    val backStackEntry = navController.getBackStackEntry("Profile")
                    hiltViewModel<ProfileViewModel>(backStackEntry)
                }else null


                PostLikesView(
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    share = post
                )
            }

            composable("CommentLikesView") {

                val previousBackStackEntry = navController.previousBackStackEntry

                val source = remember { previousBackStackEntry?.savedStateHandle?.get<String>("source") }
                val comment = remember { previousBackStackEntry?.savedStateHandle?.get<PostCommentModel>("comment") }
                val share = remember { previousBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("share") }


                LaunchedEffect(Unit) {
                    previousBackStackEntry?.savedStateHandle?.remove<String>("source")
                    previousBackStackEntry?.savedStateHandle?.remove<PostCommentModel>("comment")
                    previousBackStackEntry?.savedStateHandle?.remove<UserMealDetailModel>("share")
                }

                // ViewModels (βάσει του source)
                val homeViewModel = if (source == "home") {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.HomeViewPostCommentLikes)
                    val backStackEntry = navController.getBackStackEntry("HomeView")
                    hiltViewModel<HomeViewModel>(backStackEntry)
                } else null

                val profileViewModel = if (source == "profile") {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.ProfileViewPostCommentLikes)
                    val backStackEntry = navController.getBackStackEntry("Profile")
                    hiltViewModel<ProfileViewModel>(backStackEntry)
                } else null

                CommentLikesView(
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    comment = comment,
                    share = share
                )
            }

            composable(route = "CommentUpdateView"){
                val previousBackStackEntry = navController.previousBackStackEntry

                val comment = remember { previousBackStackEntry?.savedStateHandle?.get<PostCommentModel>("comment") }
                val source = remember { previousBackStackEntry?.savedStateHandle?.get<String>("source") }

                val homeViewModel = if (source == "home") {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.CommentUpdateProfile)
                    val backStackEntry = navController.getBackStackEntry("HomeView")
                    hiltViewModel<HomeViewModel>(backStackEntry)
                } else null

                val profileViewModel = if (source == "profile") {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.CommentUpdateHomeView)
                    val backStackEntry = navController.getBackStackEntry("Profile")
                    hiltViewModel<ProfileViewModel>(backStackEntry)
                } else null

                CommentUpdateView(
                    comment = comment,
                    homeViewModel = homeViewModel,
                    profileViewModel = profileViewModel,
                    navController = navController
                )
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
