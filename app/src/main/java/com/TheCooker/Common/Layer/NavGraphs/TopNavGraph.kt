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
import androidx.navigation.navArgument
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
import com.TheCooker.Presentation.Views.Modules.FriendRequestModule.ViewModels.FriendRequestViewModel
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

    val friendRequestViewModel = hiltViewModel<FriendRequestViewModel>()



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
                val postUpdate = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<UserMealDetailModel?>("PostRecipe")

                Log.d("PostUpdate", "Retrieved PostRecipe: $postUpdate")

                if (postUpdate != null) {
                    recipeId = postUpdate.recipeId

                }

                var hasUpdatedTopBar by remember { mutableStateOf(false) }

                if (!hasUpdatedTopBar) {
                    when {
                        topBarState.value.currentRoute == TopBarRoute.MealDetailView -> {
                            topBarState.value = topBarState.value.copy(
                                currentRoute = TopBarRoute.UpdateMealOnSearch,
                                previousRoute = TopBarRoute.MealDetailView
                            )
                        }
                        topBarState.value.currentRoute == TopBarRoute.DrawerProfile -> {
                            topBarState.value = topBarState.value.copy(
                                currentRoute = TopBarRoute.UpdatePostOnProfile,
                                previousRoute = TopBarRoute.DrawerProfile
                            )
                        }

                        postUpdate != null -> {
                            recipeId = null
                            topBarState.value = topBarState.value.copy(
                                currentRoute = TopBarRoute.UpdatePostOnProfile,
                                previousRoute = TopBarRoute.PostProfileView
                            )
                        }
                    }

                    hasUpdatedTopBar = true
                }



                Log.d("PostUpdate", postUpdate.toString())

                CreateMeal(
                    mealDetailViewModel = detailViewModel ,
                    recipeId = recipeId,
                    categoryId = sharedCategoryId ,
                    saveNavigateBack = {
                        navController.popBackStack()
                        Log.d("backFromUpdateOnNav", "RecipeId: $recipeId")

                    },
                    navController = navController,
                    combineMeals = combinedMeals,
                    mealsViewModel = mealsViewModel,
                    createMealViewModel = createMealViewModel,
                    postForUpdate = postUpdate,


                )
                }

            composable(route = "MealDetailView") {
                val navBackStackEntry = navController.previousBackStackEntry
                var detail = navController.currentBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("updatedMeal")
                Log.d("DetailOnNav", detail.toString())
                LaunchedEffect(detail) {
                    if (detail != null) {
                        Log.d("DetailOnNav", "MealDetail: $detail")
                        detailViewModel.setDetailsForPost(detail!!)
                    } else {
                        Log.d("DetailOnNav", "MealDetail is NULL, possible navigation timing issue.")
                    }
                }


                val postDetail = remember {
                    navBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("PostRecipe")
                }
                Log.d("PostDetail", postDetail.toString())



                if(postDetail != null){
                    detail = postDetail
                    detailViewModel.setDetailsForPost(postDetail)
                }
                Log.d("DetailOnNav", detail.toString())

                navController.previousBackStackEntry?.savedStateHandle?.set("updatedRecipe", detail)

                Log.d("DetailOnNav", detail.toString())

                var hasUpdatedTopBarState by remember { mutableStateOf(false) }

                if (!hasUpdatedTopBarState) {
                    if (topBarState.value.currentRoute == TopBarRoute.DrawerProfile) {
                        topBarState.value = topBarState.value.copy(
                            currentRoute = TopBarRoute.PostProfileView,
                            previousRoute = TopBarRoute.DrawerProfile
                        )
                    } else if (topBarState.value.currentRoute == TopBarRoute.Home) {
                        topBarState.value = topBarState.value.copy(
                            currentRoute = TopBarRoute.HomeViewPostView,
                            previousRoute = TopBarRoute.Home
                        )
                    }else if (topBarState.value.currentRoute == TopBarRoute.MealView) {
                        Log.d("TestRouteOnSupport", topBarState.value.currentRoute.toString())
                        topBarState.value = topBarState.value.copy(
                            currentRoute = TopBarRoute.MealDetailView,
                            previousRoute = TopBarRoute.MealView
                        )
                    }
                    else {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.MealDetailView)
                    }

                    hasUpdatedTopBarState = true
                }




                MealDetailView(
                    detailViewModel = detailViewModel,
                    updateDetails = detail
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


                // Ανάκτηση δεδομένων από previousBackStackEntry
                val mealsBeforeUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<List<UserMealModel>>("meals") ?: emptyList()
                val categoryId = navController.previousBackStackEntry?.savedStateHandle?.get<String>("categoryId") ?: ""

                val mealsAfterUpdate = navController.previousBackStackEntry?.savedStateHandle?.get<List<UserMealModel>>("mealsAfterUpdate") ?: emptyList()
                val mealsFromNav = mealsAfterUpdate.ifEmpty { mealsBeforeUpdate }

                // Το meals που θα χρησιμοποιούμε τελικά προέρχεται από το ViewModel
                val userMealState by mealsViewModel.updatedMeals.collectAsState()

                var shouldNavigate by remember { mutableStateOf(false) }

                // Μόνο την πρώτη φορά, αν το ViewModel είναι άδειο, στείλε τα meals από το navController
                LaunchedEffect(key1 = Unit) {
                    if (userMealState.isEmpty() && mealsFromNav.isNotEmpty()) {
                        mealsViewModel.setUpdatedMeals(mealsFromNav)
                    }
                }


                var hasUpdatedMealView by remember { mutableStateOf(false) }

                if (!hasUpdatedMealView) {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.MealView)
                    hasUpdatedMealView = true
                }



                MealsView(
                    apiMealsState = MealsViewModel.ApiMealsState(list = mealState.list),
                    meals = userMealState, // πλέον χρησιμοποιούμε τα meals από το ViewModel
                    navigateToDetails = { meal ->
                        detailViewModel.fetchDetails(meal.id ?: "")
                        shouldNavigate = true
                    },
                    createMeal = {
                        navController.currentBackStackEntry?.savedStateHandle?.set("categoryId", categoryId)
                        navController.navigate("CreateMeal")
                    },
                    navController = navController,
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

                var hasUpdatedHome by remember { mutableStateOf(false) }

                if (!hasUpdatedHome) {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.Home)
                    hasUpdatedHome = true
                }


                // Αν υπάρχει, χρησιμοποιούμε το ήδη υπάρχον ProfileViewModel, αλλιώς δημιουργούμε νέο
                val profileViewModel = profileBackStackEntry?.let {
                    hiltViewModel<ProfileViewModel>(it)
                } ?: hiltViewModel<ProfileViewModel>(backStackEntry)



                // Παράδοση των ViewModels στο HomeView
                HomeView(
                    navController = navController,
                    viewModel = homeViewModel,
                    profileViewModel = profileViewModel
                )
            }


            composable(
                route = "Profile?from={from}",
                arguments = listOf(navArgument("from") { defaultValue = "drawer" })
            ) { backStackEntry ->

                val previousBackStackEntry = navController.previousBackStackEntry
                val from = remember { backStackEntry.arguments?.getString("from") ?: "drawer" }

                val user = remember {
                    previousBackStackEntry?.savedStateHandle?.get<UserDataModel?>("user")
                }

                val returningFrom = remember { backStackEntry.savedStateHandle?.get<String>("returning_from") }


                var hasUpdated by remember { mutableStateOf(false) }

                LaunchedEffect(returningFrom) {
                    Log.d("TopBarState", "returningFrom: $returningFrom")
                    if (!hasUpdated) {

                        topBarState.value = topBarState.value.copy(
                            currentRoute = if (returningFrom == "likes") {
                                TopBarRoute.DrawerProfile
                            } else {
                                when (from) {
                                    "friend_request" -> TopBarRoute.JoinOnProfileFromFriendRequestView
                                    "friend_request_FromHome" -> TopBarRoute.JoinOnProfileFromHome
                                    "drawer" -> TopBarRoute.DrawerProfile
                                    else -> TopBarRoute.DrawerProfile
                                }
                            }
                        )

                        backStackEntry.savedStateHandle?.remove<String>("returning_from")
                        hasUpdated = true
                    }
                }


                LaunchedEffect(Unit) {
                    previousBackStackEntry?.savedStateHandle?.remove<UserDataModel?>("user")
                }

                val profileViewModel = hiltViewModel<ProfileViewModel>()

                ProfileView(
                    navigator = navController,
                    user = user,
                    profileViewModel = profileViewModel
                )
            }

            composable(route = "PostLikesView") {
                val previousBackStackEntry = navController.previousBackStackEntry

                val source = remember { previousBackStackEntry?.savedStateHandle?.get<String>("source") }
                val post = remember { previousBackStackEntry?.savedStateHandle?.get<UserMealDetailModel>("post") }


                var hasUpdated by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    if (!hasUpdated) {
                        previousBackStackEntry?.savedStateHandle?.remove<String>("source")
                        previousBackStackEntry?.savedStateHandle?.remove<UserMealDetailModel>("post")
                        previousBackStackEntry?.savedStateHandle?.remove<UserMealDetailModel>("friend_request_FromHome")
                        hasUpdated = true
                    }
                }

                var hasUpdatedHomeViewModel by remember { mutableStateOf(false) }

                val homeViewModel = if (source == "home") {
                    if (!hasUpdatedHomeViewModel) {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.HomeViewPostLikes)
                        hasUpdatedHomeViewModel = true
                    }

                    val backStackEntry = navController.getBackStackEntry("HomeView")
                    hiltViewModel<HomeViewModel>(backStackEntry)
                } else null

                val viewModel = if (source == "profile") {
                    Log.d("TestRouteLikesComposable", topBarState.value.currentRoute.toString())

                    if (!hasUpdated) {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.ProfileViewPostLikes)
                        hasUpdated = true
                    }

                    val backStackEntry = navController.getBackStackEntry("Profile")
                    hiltViewModel<ProfileViewModel>(backStackEntry)
                } else null

                PostLikesView(
                    homeViewModel = homeViewModel,
                    profileViewModel = viewModel,
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
                var hasUpdatedHomePostCommentLikes by remember { mutableStateOf(false) }

                val homeViewModel = if (source == "home") {
                    if (!hasUpdatedHomePostCommentLikes) {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.HomeViewPostCommentLikes)
                        hasUpdatedHomePostCommentLikes = true
                    }

                    val backStackEntry = navController.getBackStackEntry("HomeView")
                    hiltViewModel<HomeViewModel>(backStackEntry)
                } else null

                var hasUpdatedProfilePostCommentLikes by remember { mutableStateOf(false) }

                val profileViewModel = if (source == "profile") {
                    if (!hasUpdatedProfilePostCommentLikes) {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.ProfileViewPostCommentLikes)
                        hasUpdatedProfilePostCommentLikes = true
                    }

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

                var hasUpdatedHomeViewModel by remember { mutableStateOf(false) }
                var hasUpdatedProfileViewModel by remember { mutableStateOf(false) }

                val homeViewModel = if (source == "home") {
                    if (!hasUpdatedHomeViewModel) {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.CommentUpdateProfile)
                        hasUpdatedHomeViewModel = true // ✅ Εξασφαλίζουμε ότι ενημερώνεται μόνο μία φορά
                    }

                    val backStackEntry = navController.getBackStackEntry("HomeView")
                    hiltViewModel<HomeViewModel>(backStackEntry)
                } else null

                val profileViewModel = if (source == "profile") {
                    if (!hasUpdatedProfileViewModel) {
                        topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.CommentUpdateHomeView)
                        hasUpdatedProfileViewModel = true // ✅ Εξασφαλίζουμε ότι ενημερώνεται μόνο μία φορά
                    }

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

                FriendRequestView(navController, friendRequestViewModel)
            }




            composable(route = TopBarMenuModel.NotificationView.route) {
                var hasUpdatedNotificationView by remember { mutableStateOf(false) }
                if (!hasUpdatedNotificationView) {
                    topBarState.value = topBarState.value.copy(currentRoute = TopBarRoute.FriendRequestView)
                    hasUpdatedNotificationView = true
                }

                NotificationView()
            }

        }
    }
}
