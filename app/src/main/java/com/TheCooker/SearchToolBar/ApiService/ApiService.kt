package com.TheCooker.SearchToolBar.ApiService

import com.TheCooker.SearchToolBar.RecipeRepo.CategoriesResponse
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategory
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategoryResponse
import retrofit2.http.GET
import retrofit2.http.Query



interface ApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    @GET("filter.php")
    suspend fun getMeals(@Query("c") category: String): MealsCategoryResponse

    @GET("search.php")
    suspend fun getMealDetail(@Query("s") meal: String): MealsDetailsResponse
}