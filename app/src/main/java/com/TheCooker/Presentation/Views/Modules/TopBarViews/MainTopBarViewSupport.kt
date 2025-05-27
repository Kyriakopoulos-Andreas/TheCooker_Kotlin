package com.TheCooker.Presentation.Views.Modules.TopBarViews

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.TheCooker.Presentation.Views.Modules.DrawerModule.ViewModels.DrawerViewModel
import com.TheCooker.Presentation.Views.Modules.DrawerModule.Views.LogOutAlertDialog
import com.TheCooker.Domain.Layer.UseCase.GoogleIntents.GoogleClient
import com.TheCooker.Presentation.Views.Modules.LoginModule.ViewModels.LoginViewModel
import com.TheCooker.Common.Layer.NavGraphs.TopNavGraph
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.UseCase.Location.Permissions.LocationUtils
import com.TheCooker.Domain.Layer.UseCase.Location.LocationViewModel

import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.BottomSheetMealDetailMenu
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel
import com.TheCooker.Presentation.Views.Modules.SharedModule.SharedViewModel
import com.example.cooker.ListView.DrawerContent

import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "StateFlowValueCalledInComposition"
)
@Composable
fun MainTopBarViewSupport(
    userData: UserDataModel?,
    googleClient: GoogleClient,
    navControllerLog: NavHostController,
    loginViewModel: LoginViewModel,
    createMealViewModel: CreateMealViewModel,
    mealsDetailViewModel: MealsDetailViewModel,
    mealsViewModel: MealsViewModel,
    categoryViewModel: CategoryViewModel,
    topBarState: MutableState<TopBarState>

    ) {

    val locationViewModel: LocationViewModel = hiltViewModel()
    val viewModel: DrawerViewModel = viewModel()
    val meal by mealsDetailViewModel.mealsDetailState.observeAsState()
    val currentScreen = remember {
        viewModel.currentScreen.value
    }
    val title = remember { mutableStateOf(currentScreen.title) }

    val menuTopBarRoute = remember {
        mutableStateOf(false)
    }
    val previousRoute = remember { mutableStateOf<String?>(null) }

    val dialogOpenLogOut = remember {
        mutableStateOf(false)
    }
    val dialogOpenDeleteRecipe = remember {
        mutableStateOf(false)
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedViewModel = hiltViewModel<SharedViewModel>()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val coarseGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            val fineGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val notificationGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[android.Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else {
                true
            }



            if ((coarseGranted || fineGranted) && notificationGranted) {
//                Toast.makeText(context, "Permission Accepted", Toast.LENGTH_LONG).show()

                // Request location updates
                scope.launch {
                    locationViewModel.requestLocation(context)

                    // After getting the location permission, check if GPS is enabled
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                    if (!isGpsEnabled) {
                        // If GPS is disabled, open the location settings
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                }
            } else {
                // Show rationale message if permission is denied
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (rationaleRequired) {
                    Toast.makeText(
                        context,
                        "Location permission is required for better user experience",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    )





    LaunchedEffect(Unit) {
        loginViewModel.updateFcmToken()
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (!LocationUtils.hasLocationPermission(context)) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            locationViewModel.requestLocation(context)
            //Log.d("UserLocation", locationViewModel.location.value.toString())
        }
    }


    LaunchedEffect(locationViewModel.location.value) {
        val location = locationViewModel.location.value

        if (location != null) {
            // Ενημερώσαμε την τοποθεσία, τώρα μπορείς να το δείξεις στο Toast
            Toast.makeText(context, "Fixed address: $location", Toast.LENGTH_LONG).show()
            //Log.d("UserLocation1", "Fixed address: $location")
        } else {
            // Η τοποθεσία δεν έχει ενημερωθεί ακόμα, μπορείς να δείξεις κάτι άλλο αν θες
            //Log.d("UserLocation", "Location is still null")
        }
    }










    val closeDrawer: () -> Unit = {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }


    val onDrawerItemSelected: (String) -> Unit = { route ->
        previousRoute.value = currentRoute
        closeDrawer()

        when (route) {
            "Profile" -> topBarState.value =
                topBarState.value.copy(currentRoute = TopBarRoute.DrawerProfile)

            "Calendar" -> topBarState.value =
                topBarState.value.copy(currentRoute = TopBarRoute.DrawerCalendar)

            "Options" -> topBarState.value =
                topBarState.value.copy(currentRoute = TopBarRoute.DrawerOptions)

            "Help" -> topBarState.value =
                topBarState.value.copy(currentRoute = TopBarRoute.DrawerHelp)

            "Information" -> topBarState.value =
                topBarState.value.copy(currentRoute = TopBarRoute.DrawerInformation)

            "LogOut" -> dialogOpenLogOut.value = true
        }

        navController.navigate(route) {
            popUpTo(route) { inclusive = false }
            launchSingleTop = true
        }
    }



        val userMealDetailExists = remember { mutableStateOf(false) }
    val apiMealDetailExists = remember { mutableStateOf(false) }


    LaunchedEffect(meal) {
        userMealDetailExists.value = meal?.list?.any { it is MealsDetailViewModel.recipeDetails.UserMealDetail } == true
        apiMealDetailExists.value = meal?.list?.any { it is MealsDetailViewModel.recipeDetails.ApiMealDetail } == true


        Log.d("DetailExistsUser", userMealDetailExists.value.toString())
        Log.d("DetailExistsApi", apiMealDetailExists.value.toString())
    }



    BottomSheetMealDetailMenu(
        mealDetail = mealsDetailViewModel,
        dialogOpen = dialogOpenDeleteRecipe,
        navController = navController,
        mealsViewModel = mealsViewModel ,
        mealId = mealsDetailViewModel.mealsDetailState.value?.list?.filterIsInstance<MealsDetailViewModel.recipeDetails.UserMealDetail>() // Φιλτράρω μόνο τα UserMealDetail
            ?.flatMap { it.mealDetail } // Παίρνω την λίστα με τα mealDetail
            ?.firstOrNull()?.recipeId ?: "",
        content = { modalSheetState, showModalSheet ->  // Οι δύο lambda που πέρνει ως όρισμα η  BottomSheetMealDetailMenu. Η μια κρατάει την κατάσταση (Hidden, show) του sheet(modalSheetState)
            Scaffold(
                                     // Ενώ η άλλη αλλάζει την κατάσταση του
                scaffoldState = scaffoldState,                          // Ο χρήστης κάνει click στην showModalSheet και καλείτε η content του BottomSheetMealDetailMenu η οποία αλλάζει την κατάσταση
                topBar =
                {

                    when(topBarState.value.currentRoute){
                        TopBarRoute.Home -> {
                            Log.d("TestRouteOnSupport", topBarState.value.currentRoute.toString())
                            TopMenu(
                                navBackStackEntry = navBackStackEntry,
                                navController = navController,
                                scaffoldState = scaffoldState,
                                scope = scope,
                                previousRoute = previousRoute,
                                sharedViewModel = sharedViewModel
                            )
                            title.value = ""
                        }
                        TopBarRoute.MealView -> {
                            Log.d("TestRouteOnSupport", topBarState.value.currentRoute.toString())
                            TopMenu(
                                navBackStackEntry = navBackStackEntry,
                                navController = navController,
                                scaffoldState = scaffoldState,
                                scope = scope,
                                previousRoute = previousRoute,
                                sharedViewModel = sharedViewModel
                            )
                            title.value = ""
                        }


                        TopBarRoute.FriendRequestView -> {
                            Log.d("TestRouteOnSupport", topBarState.value.currentRoute.toString())
                            TopMenu(
                                navBackStackEntry = navBackStackEntry,
                                navController = navController,
                                scaffoldState = scaffoldState,
                                scope = scope,
                                previousRoute = previousRoute,
                                sharedViewModel = sharedViewModel
                            )
                            title.value = ""
                        }




                        TopBarRoute.DrawerCalendar -> {
                            SecondaryTopBarView(
                            title = title.value,
                            topBar = topBarState.value,
                            navController,
                            scaffoldState,
                            scope,
                            previousRoute,
                            false,
                            openBottomSheetMealDetailMenu = showModalSheet,
                            mealDetail = mealsDetailViewModel,
                            mealsViewModel = mealsViewModel, sharedViewModel = sharedViewModel
                        )
                        }
                        TopBarRoute.DrawerOptions -> {
                            SecondaryTopBarView(
                                title = title.value,
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }
                        TopBarRoute.DrawerHelp -> {
                            SecondaryTopBarView(
                                title = title.value,
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }
                        TopBarRoute.DrawerInformation -> {
                            SecondaryTopBarView(
                                title = title.value,
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }
                        TopBarRoute.CreateMeal -> {
                            SecondaryTopBarView(
                                title = title.value,
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        TopBarRoute.ProfileViewPostCommentLikes -> {
                            SecondaryTopBarView(
                                title = "Comment Likes",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        TopBarRoute.HomeViewPostCommentLikes -> {
                            SecondaryTopBarView(
                                title = "Comment Likes",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )

                        }

                        TopBarRoute.MealDetailView -> {
                            if (userMealDetailExists.value) {
                                title.value = ""
                                SecondaryTopBarView(
                                    title = title.value,
                                    topBar = topBarState.value,
                                    navController,
                                    scaffoldState,
                                    scope,
                                    previousRoute,
                                    true,
                                    openBottomSheetMealDetailMenu = showModalSheet,
                                    mealDetail = mealsDetailViewModel,
                                    mealsViewModel = mealsViewModel,
                                    sharedViewModel = sharedViewModel
                                )
                            } else if (apiMealDetailExists.value) {
                                title.value = ""
                                SecondaryTopBarView(

                                    title = title.value,
                                    topBar = topBarState.value,
                                    navController,
                                    scaffoldState,
                                    scope,
                                    previousRoute,
                                    false,
                                    openBottomSheetMealDetailMenu = showModalSheet,
                                    mealDetail = mealsDetailViewModel,
                                    mealsViewModel = mealsViewModel,
                                    sharedViewModel = sharedViewModel
                                )
                            }
                        }
                        TopBarRoute.DrawerProfile -> {
                            SecondaryTopBarView(
                            title = "Profile",
                            topBar = topBarState.value,
                            navController,
                            scaffoldState,
                            scope,
                            previousRoute,
                            isUserRecipe = true,
                            openBottomSheetMealDetailMenu = showModalSheet,
                            mealDetail = mealsDetailViewModel,
                            mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                        )
                        }
                        TopBarRoute.UpdatePostOnProfile -> {
                            SecondaryTopBarView(
                            title = "Update Post",
                            topBar = topBarState.value,
                            navController,
                            scaffoldState,
                            scope,
                            previousRoute,
                            isUserRecipe = false,
                            openBottomSheetMealDetailMenu = showModalSheet,
                            mealDetail = mealsDetailViewModel,
                            mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                        )
                        }
                        TopBarRoute.UpdateMealOnSearch -> {
                            SecondaryTopBarView(
                            title = "UpdateMealOnSearch",
                            topBar = topBarState.value,
                            navController,
                            scaffoldState,
                            scope,
                            previousRoute,
                            isUserRecipe = false,
                            openBottomSheetMealDetailMenu = showModalSheet,
                            mealDetail = mealsDetailViewModel,
                            mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                        )

                        }

                        TopBarRoute.CommentUpdateProfile -> {
                            SecondaryTopBarView(
                                title = "Comment Update",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        TopBarRoute.CommentUpdateHomeView -> {
                            SecondaryTopBarView(
                                title = "Comment Update",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }



                        TopBarRoute.PostProfileView -> {
                            SecondaryTopBarView(
                                title = "Post",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        TopBarRoute.ProfileViewPostLikes -> {
                            SecondaryTopBarView(
                                title = "Likes",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        TopBarRoute.JoinOnProfileFromFriendRequestView -> {
                            SecondaryTopBarView(
                                title = "",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }
                        TopBarRoute.JoinOnProfileFromHome -> {
                            SecondaryTopBarView(
                                title = "",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }

                        TopBarRoute.HomeViewPostView -> {
                            SecondaryTopBarView(
                                title = "Post",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )

                        }

                        TopBarRoute.HomeViewPostLikes -> {
                            SecondaryTopBarView(
                                title = "Likes",
                                topBar = topBarState.value,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                isUserRecipe = false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel,
                                sharedViewModel = sharedViewModel
                            )
                        }




                    }
                },
                drawerContent = {
                    DrawerContent(
                        title = title,
                        onItemSelected = onDrawerItemSelected,
                        closeDrawer = closeDrawer,
                        currentRoute = currentRoute
                            ?: "",  // Περιεχόμενο του Drawer
                        navcontroller = navController,
                        dialogOpen = dialogOpenLogOut,
                        menuTopBarRoute
                    ) },
                drawerBackgroundColor = Color(0xFF202020),
                ) { paddingValues ->
                TopNavGraph(
                    navController = navController,
                    paddingValues = paddingValues,
                    userData,
                    googleClient,
                    navControllerLog,
                    loginViewModel,
                    createMealViewModel = createMealViewModel,
                    detailViewModel = mealsDetailViewModel,
                    mealsViewModel = mealsViewModel,
                    categoryViewModel = categoryViewModel,
                    topBarState
                )
                if (dialogOpenLogOut.value) {
                    LogOutAlertDialog(
                        dialogOpenLogOut,
                        googleClient = googleClient,
                        navController = navControllerLog,
                        loginViewModel
                    )
                }
            }
        }
    )
}



