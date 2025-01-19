package com.TheCooker.Presentation.Views.Modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarMenuModel

@Composable
fun FriendRequestView(){

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text = "FriendRequestView",
            modifier = Modifier.clickable {  })


    }


}