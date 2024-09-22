package com.TheCooker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.TheCooker.SearchToolBar.ApiService.ApiService
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltWorker
class SyncMealsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val recipeRepo: RecipeRepo
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SyncMealsWorker", "Work started")
        val categoryIds = listOf("Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian" ,"Breakfast", "Goat")

        return try {
            for (categoryId in categoryIds) {
                Log.d("SyncMealsWorker", "Fetching meals for category: $categoryId")
                val response = apiService.getMeals(categoryId)
                val apiMeals = response.meals
                Log.d("SyncMealsWorker", "Fetched ${apiMeals.size} meals for category: $categoryId")
                recipeRepo.syncApiMealsWithFirebase(categoryId, apiMeals)
                Log.d("SyncMealsWorker", "Synced meals for category: $categoryId")
            }
            Log.d("SyncMealsWorker", "Work completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncMealsWorker", "Error syncing meals", e)
            Result.failure()
        }
    }

}

