package com.TheCooker.Menu

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.TopAppBar
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.TheCooker.Drawer.DrawerViewModel
import com.TheCooker.Drawer.LogOutAlertDialog
import com.TheCooker.Login.Authentication.GoogleAuth.GoogleClient
import com.TheCooker.Login.LoginViewModel
import com.TheCooker.NavGraphs.TopNavGraph
import com.TheCooker.Login.SignIn.UserData
import com.example.cooker.ListView.DrawerContent
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuView(
    userData: UserData?,
    googleClient: GoogleClient,
    navControllerLog: NavHostController,
    loginViewModel: LoginViewModel
){

    val viewModel: DrawerViewModel = viewModel()
    val currentScreen = remember{
        viewModel.currentScreen.value
    }
    val title = remember{ mutableStateOf(currentScreen.title) }

    val topBarRoute = remember{
        mutableStateOf(false)
    }
    val previousRoute = remember { mutableStateOf<String?>(null) }


    val dialogOpen = remember{
        mutableStateOf(false)
    }

    // Find us out on which "View" we currently are
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
        topBarRoute.value = true
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
    }








    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if(!topBarRoute.value){
                TopMenu(navBackStackEntry, navController, scaffoldState, scope, previousRoute)
            }else if(topBarRoute.value)
            {
                DrawerTopBar(title = title.value, topBarRoute, navController, scaffoldState, scope, previousRoute)
            }


        },


        drawerContent = {

                DrawerContent(
                    title = title,
                    onItemSelected =onDrawerItemSelected,
                    closeDrawer = closeDrawer,
                    currentRoute = currentRoute ?: "",  // Περιεχόμενο του Drawer
                    navcontroller = navController,
                    dialogOpen = dialogOpen,
                    topBarRoute
                )

        },
        drawerBackgroundColor = Color(0xFF202020),


    ) { paddingValues ->
        TopNavGraph(navController = navController, paddingValues = paddingValues, userData, googleClient, navControllerLog, loginViewModel, topBarRoute)
        if (dialogOpen.value) {
            LogOutAlertDialog(dialogOpen, googleClient = googleClient, navController = navControllerLog, loginViewModel)
        }
    }
}




