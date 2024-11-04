package com.TheCooker.Drawer.Calendar


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.TheCooker.Menu.topBars


@Composable
fun Calendar(topBar: topBars){

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        androidx.compose.material3.Text(text = "CalendarScreen",
            color = Color.White)
        topBar.drawerMenuRoute= true
        topBar.menuTopBarRoute = false
        topBar.mealTopBarRoute = false


    }
}