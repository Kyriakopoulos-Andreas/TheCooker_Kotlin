package com.TheCooker.Menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScaffoldState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.TheCooker.NavGraphs.TopBarMenu
import com.TheCooker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopMenu(
    navBackStackEntry: NavBackStackEntry?,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    previousRoute: MutableState<String?>
) {
    var selectedItem by rememberSaveable {
        mutableStateOf(2)
    }

    LaunchedEffect(navBackStackEntry) {
        val currentRoute = navBackStackEntry?.destination?.route
        selectedItem = TopBarMenu.itemsList.indexOfFirst { it.route == currentRoute }
    }

    NavigationBar(
        tonalElevation = 4.dp,
        containerColor = Color(0xFF202020),
        modifier = Modifier.height(85.dp)
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
                                scope.launch {
                                    previousRoute.value = navController.currentBackStackEntry?.destination?.route
                                    scaffoldState.drawerState.open()
                                }
                            } else {
                                selectedItem = index
                                navController.navigate(screen.route) {
                                    // Καθαρισμός του back stack
                                    popUpTo(TopBarMenu.HomeView.route) {
                                        inclusive = true // Μην διαγράψετε την αρχική οθόνη
                                    }
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
}

