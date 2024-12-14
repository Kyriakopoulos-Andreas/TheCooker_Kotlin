package com.TheCooker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.TheCooker.dataLayer.Api.ApiService
import com.TheCooker.Domain.Layer.Repositories.RecipeRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncMealsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val recipeRepo: RecipeRepo
) : CoroutineWorker(context, workerParams) {



    override suspend fun doWork(): Result {
        Log.d("SyncMealsWorker", "Starting work")
        if (!recipeRepo.checkIfAdmin()) {
            Log.d("SyncMealsWorker", "User is not admin, skipping sync")
            return Result.success()
        }
        Log.d("SyncMealsWorker", "User is admin, proceeding with sync")

        val categoryIds = listOf("Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat")

        return try {
            val response = apiService.getCategories()
            recipeRepo.syncApiCategoriesWithFirebase(response.categories)

            for (categoryName in categoryIds) {
                Log.d("SyncMealsWorker", "Fetching meals for category: $categoryName")
                val response = apiService.getMeals(categoryName)

                val apiMeals = response.meals
                Log.d("ApiMeals", apiMeals.size.toString())
                Log.d("SyncMealsWorker", "Fetched ${apiMeals.size} meals for category: $categoryName")

                val categoryId = recipeRepo.generateId(categoryName)

                for (meal in apiMeals) {
                    val mealWithCategoryId = meal.copy(categoryId = categoryId)
                    recipeRepo.syncApiMealsWithFirebase(mealWithCategoryId.categoryId ?: "", listOf(mealWithCategoryId))
                }
                Log.d("SyncMealsWorker", "Synced meals for category: $categoryName")
            }

            Log.d("SyncMealsWorker", "Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncMealsWorker", "Error syncing meals", e)
            Result.failure()
        }
    }


}

