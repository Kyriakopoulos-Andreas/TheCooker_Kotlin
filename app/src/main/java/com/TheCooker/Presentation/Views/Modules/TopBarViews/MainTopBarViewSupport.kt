package com.TheCooker.Presentation.Views.Modules.TopBarViews

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
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
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarsModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.Views.BottomSheetMealDetailMenu
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel
import com.example.cooker.ListView.DrawerContent
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
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

    ) {

    val meal by mealsDetailViewModel.mealsDetailState.observeAsState()

    val topBar by remember {
        mutableStateOf(
            TopBarsModel(
                menuTopBarRoute = true,
                mealTopBarRoute = false,
                drawerMenuRoute = false,
                updateBar = false
            )
        )
    }


    val viewModel: DrawerViewModel = viewModel()
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


    val closeDrawer: () -> Unit = {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }


    val onDrawerItemSelected: (String) -> Unit = { route ->
        previousRoute.value = currentRoute
        closeDrawer()
        topBar.menuTopBarRoute = false
        topBar.drawerMenuRoute = true
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId)
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
        topBar = topBar,
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
                topBar = {                                              // Ένωσα τα 3 κομμάτια στο MenuView. Όταν καλείται η showModalSheet lambda μεσα στο ExtraTopBar τότε επιρεάζεται το BottomSheetMealDetailMenu και τρέχει το content
                    if (topBar.menuTopBarRoute) {                       // Για αυτό κάναμε wrap το scaffold μεσα στην BottomSheetMealDetailMenu. Ετσι έχουμε την δυνατότητα να επαναχρησιμοποιησουμε το sheet και σε αλλα μεροι χωρις να αλλάξει η δομη του GUI
                        TopMenu(
                            navBackStackEntry,
                            navController,
                            scaffoldState,
                            scope,
                            previousRoute
                        )
                        title.value = ""
                    } else if (topBar.drawerMenuRoute) {
                        SecondaryTopBarView(
                            title = title.value,
                            topBar = topBar,
                            navController,
                            scaffoldState,
                            scope,
                            previousRoute,
                            false,
                            openBottomSheetMealDetailMenu = showModalSheet,
                            mealDetail = mealsDetailViewModel,
                            mealsViewModel = mealsViewModel
                        )
                    } else if (topBar.mealTopBarRoute) {
                        Log.d("111", userMealDetailExists.value.toString())
                        Log.d("TopBarStatusMain1", "mealTopBarRoute: ${topBar.mealTopBarRoute}, updateBar: ${topBar.updateBar}")
                        if (userMealDetailExists.value) {
                            Log.d("TopBarStatusMain", "mealTopBarRoute: ${topBar.mealTopBarRoute}, updateBar: ${topBar.updateBar}")
                            title.value = ""
                            SecondaryTopBarView(
                                title = title.value,
                                topBar = topBar,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                true,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel
                            )
                        }else if (apiMealDetailExists.value) {
                            title.value = ""
                            SecondaryTopBarView(
                                title = title.value,
                                topBar = topBar,
                                navController,
                                scaffoldState,
                                scope,
                                previousRoute,
                                false,
                                openBottomSheetMealDetailMenu = showModalSheet,
                                mealDetail = mealsDetailViewModel,
                                mealsViewModel = mealsViewModel
                            )
                        }
                    } },
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
                    topBar,
                    createMealViewModel = createMealViewModel,
                    detailViewModel = mealsDetailViewModel,
                    mealsViewModel = mealsViewModel,
                    categoryViewModel = categoryViewModel
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




