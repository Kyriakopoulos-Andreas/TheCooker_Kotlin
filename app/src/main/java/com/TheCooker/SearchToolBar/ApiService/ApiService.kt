package com.TheCooker.SearchToolBar.ApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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