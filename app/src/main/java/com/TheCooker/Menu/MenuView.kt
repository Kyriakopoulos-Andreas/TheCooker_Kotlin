package com.TheCooker.Menu

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.TheCooker.NavGraphs.TopBarMenu
import com.TheCooker.NavGraphs.TopNavGraph
import com.TheCooker.R
import com.TheCooker.Login.Authentication.GoogleAuth.UserData



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuView(userData: UserData?){
    val navController = rememberNavController()



    var selectedItem by rememberSaveable{
        mutableStateOf(2)
    }


    Scaffold(
        topBar = {

            NavigationBar (tonalElevation = 4.dp,
                containerColor = Color(0xFF202020)
            ){



                Column {
                    Box(modifier = Modifier.fillMaxWidth(), ){
                        Image(
                            painter = painterResource(id = R.drawable.logo_white), // Εικόνα λογότυπου
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopCenter) // Τοποθέτηση στο κέντρο της κορυφής // Απόσταση από την κορυφή
                                .size(47.dp)
                                .padding(top = 8.dp),

                            contentScale = ContentScale.Fit // Διατήρηση αναλογιών
                        )

                    }


                    Row {
                        TopBarMenu.itemsList.forEachIndexed { index, screen ->
                            NavigationBarItem(
                                selected = selectedItem == index ,

                                onClick = {
                                    selectedItem = index
                                    navController.navigate(screen.route){
                                        popUpTo(navController.graph.findStartDestination().id)
                                        launchSingleTop = true
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
                                    },) {
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
                                ),

                                )
                        }
                    }
                }
            }
        }
    ) {

        TopNavGraph(navController = navController, paddingValues = it, userData)
    }

    
}
