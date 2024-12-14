package com.TheCooker.dataLayer.Api

import com.TheCooker.Domain.Layer.Models.RecipeModels.CategoriesResponse
import com.TheCooker.Domain.Layer.Models.RecipeModels.MealsCategoryResponse
import com.TheCooker.Domain.Layer.Models.RecipeModels.MealsDetailsResponse
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
