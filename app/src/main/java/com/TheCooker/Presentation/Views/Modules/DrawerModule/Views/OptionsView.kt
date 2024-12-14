package com.TheCooker.Presentation.Views.Modules.DrawerModule.Views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarsModel

@Composable
fun Options(topBar: TopBarsModel){

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        androidx.compose.material3.Text(text = "OptionsScreen",
            color = Color.White)
        topBar.drawerMenuRoute = true


    }
}