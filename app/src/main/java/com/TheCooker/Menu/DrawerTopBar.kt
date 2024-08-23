package com.TheCooker.Menu

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerTopBar(
    title: String,
    topBarRoute: MutableState<Boolean>,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    previousRoute: MutableState<String?>
){

    BackHandler {
        if (topBarRoute.value) {
            scope.launch {
                topBarRoute.value = false
                scaffoldState.drawerState.open()
                previousRoute.value?.let { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }

                }
            }

        }
    }

    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = Color(0xFF202020),
        modifier = Modifier.height(50.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    scope.launch {
                        topBarRoute.value = false
                        scaffoldState.drawerState.open()
                        previousRoute.value?.let { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Sharp.ArrowBack, contentDescription = "Back",
                        modifier = Modifier.size(30.dp))
                }
            }

            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )

        }

     

    }

}