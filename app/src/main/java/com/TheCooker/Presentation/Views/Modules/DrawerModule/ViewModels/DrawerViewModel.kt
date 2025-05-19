package com.TheCooker.Presentation.Views.Modules.DrawerModule.ViewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.TheCooker.Domain.Layer.Models.ScreenModels.DrawerScreensModel

class DrawerViewModel: ViewModel() {
    private val _currentScreen: MutableState<DrawerScreensModel> = mutableStateOf(DrawerScreensModel.drawerScreensList[0])
    val currentScreen : MutableState<DrawerScreensModel>get() = _currentScreen
}