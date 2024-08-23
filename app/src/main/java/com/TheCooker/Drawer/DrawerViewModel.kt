package com.TheCooker.Drawer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel

class DrawerViewModel: ViewModel() {

    private val _currentScreen: MutableState<DrawerScreens> = mutableStateOf(DrawerScreens.drawerScreensList[0])

    val currentScreen : MutableState<DrawerScreens>get() = _currentScreen

    val _dialogOpen: MutableState<Boolean> = mutableStateOf(false)
    val dialogOpen: MutableState<Boolean>get() = _dialogOpen


    fun setCurrentScreen(screen: DrawerScreens) {
        _currentScreen.value = screen
    }




}