package com.TheCooker.SearchToolBar.ApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

val retrofit = Retrofit.Builder().baseUrl("https://www.themealdb.com/api/json/v1/1/").
addConverterFactory(GsonConverterFactory.create()).build()

val recipeService = retrofit.create(ApiService::class.java)

interface ApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    @GET("filter.php")
    suspend fun getMeals(@Query("c") category: String): MealsCategoryResponse

    @GET("search.php")
    suspend fun getMealDetail(@Query("s") meal: String): MealsDetailsResponse
}