package com.TheCooker.Menu

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.TheCooker.NavGraphs.TopBarMenu
import com.TheCooker.NavGraphs.TopNavGraph
import com.TheCooker.R
import com.TheCooker.Login.SignIn.UserData
import com.example.cooker.ListView.DrawerContent
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuView(userData: UserData?) {

    // Find us out on which "View" we currently are
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    var selectedItem by rememberSaveable {
        mutableStateOf(2)
    }

    val closeDrawer: () -> Unit = {
        scope.launch {
            scaffoldState.drawerState.close()
        }
    }

    val onDrawerItemSelected: (String) -> Unit = {
        closeDrawer()
    }

    androidx.compose.material.Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            NavigationBar(
                tonalElevation = 4.dp,
                containerColor = Color(0xFF202020),
                modifier = Modifier.height(100.dp)
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_white),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .size(47.dp)
                                .padding(top = 2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Row(modifier = Modifier.padding(top = 0.dp)) {
                        TopBarMenu.itemsList.forEachIndexed { index, screen ->
                            NavigationBarItem(
                                selected = selectedItem == index,
                                onClick = {
                                    if (index == 0) {
                                        // Άνοιγμα του Drawer όταν πατηθεί το πρώτο εικονίδιο
                                        scope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    } else {
                                        selectedItem = index
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id)
                                            launchSingleTop = true
                                        }
                                    }
                                },
                                icon = {
                                    BadgedBox(badge = {
                                        if (screen.badgeCount != null) {
                                            Badge {
                                                Text(text = screen.badgeCount.toString())
                                            }
                                        } else if (screen.hasNEWS) {
                                            Badge()
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (index == selectedItem) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = null
                                        )
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.Black,
                                    unselectedIconColor = Color.White,
                                    indicatorColor = Color(0xFFFFC107)
                                )
                            )
                        }
                    }
                }
            }
        },
        drawerContent = {
            DrawerContent(onItemSelected = onDrawerItemSelected, closeDrawer = closeDrawer,
                currentRoute = currentRoute?: "")  // Περιεχόμενο του Drawer
        }
    ) { paddingValues ->
        TopNavGraph(navController = navController, paddingValues = paddingValues, userData)
    }
}

