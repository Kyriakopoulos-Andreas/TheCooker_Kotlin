package com.TheCooker.SearchToolBar.RecipeRepo

data class UserRecipe (
    val recipeId: String,
    val recipeName: String,
    val recipeIngredients: List<String>,
    val steps: List<String>,
    val recipeImage: String? = null,
    val creatorId: String,
    val timestamp: Long = System.currentTimeMillis()

)
data class UserResponse(val userMeals: List<UserRecipe>)

