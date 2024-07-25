package com.TheCooker.SearchToolBar.ApiService

data class MealsCategory(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String
)

data class MealsCategoryResponse(val meals: List<MealsCategory>)


