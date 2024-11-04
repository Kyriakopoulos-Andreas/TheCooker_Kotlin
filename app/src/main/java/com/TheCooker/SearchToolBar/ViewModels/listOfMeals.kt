package com.TheCooker.SearchToolBar.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.TheCooker.SearchToolBar.RecipeRepo.MealDetail
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
import javax.inject.Singleton

@Singleton
object listOfMeals {
    private var mealsViewModel: MealsViewModel? = null

    fun initialize(viewModel: MealsViewModel) {
        mealsViewModel = viewModel
    }

    val combinedMeals: LiveData<MutableList<MealItem>>
        get() {
            return mealsViewModel?.combinedMeals ?: MutableLiveData()
        }
}