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
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategory
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
        Log.d("SyncMealsWorker", "Starting work")
        if (!recipeRepo.checkIfAdmin()) {
            Log.d("SyncMealsWorker", "User is not admin, skipping sync")
            return Result.success()
        }
        Log.d("SyncMealsWorker", "User is admin, proceeding with sync")


        val categoryIds = listOf("Beef", "Chicken", "Dessert", "Lamb", "Miscellaneous", "Pasta", "Pork", "Seafood", "Side", "Starter", "Vegan", "Vegetarian", "Breakfast", "Goat")

        return try {
            for(category in categoryIds){
                Log.d("FetchingCategories", "Fetching category  $category")
            }



            for (categoryName in categoryIds) {
                Log.d("SyncMealsWorker", "Fetching meals for category: $categoryName")
                val response = apiService.getMeals(categoryName)

                // Έλεγχος αν η απάντηση είναι null ή αν το meals είναι null
                val apiMeals = response.meals ?: emptyList()
                Log.d("SyncMealsWorker", "Fetched ${apiMeals.size} meals for category: $categoryName")

                // Δημιουργία του hash ID για την κατηγορία
                val categoryId = recipeRepo.generateId(categoryName)

                // Αποθήκευση κάθε γεύματος στη βάση δεδομένων με το κατάλληλο categoryId
                for (meal in apiMeals) {
                    val mealWithCategoryId = meal.copy(categoryId = categoryId) // Χρησιμοποιούμε το hash ID της κατηγορίας
                    recipeRepo.syncApiMealsWithFirebase(mealWithCategoryId.categoryId ?: "", listOf(mealWithCategoryId)) // Στέλνουμε το γεύμα
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

