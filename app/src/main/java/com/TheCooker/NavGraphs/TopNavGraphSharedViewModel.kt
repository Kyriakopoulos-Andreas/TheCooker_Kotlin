package com.TheCooker.NavGraphs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
import dagger.hilt.android.lifecycle.HiltViewModel

//I used this class to remember the categoryId when navigating from details of meal back at meals view
class TopNavGraphSharedViewModel: ViewModel() {
    private val _categoryId = MutableLiveData<String>()
    val categoryId: LiveData<String> get() = _categoryId

    fun setCategoryId(id: String) {
        _categoryId.value = id
    }


}