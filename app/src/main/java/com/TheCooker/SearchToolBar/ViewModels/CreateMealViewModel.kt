package com.TheCooker.SearchToolBar.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CreateMealViewModel: ViewModel()  {

    private val _mealName = mutableStateOf("")
    val mealName: State<String> = _mealName

    fun onMealNameChange(newValue: String) {
        _mealName.value = newValue
    }
}