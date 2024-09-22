package com.TheCooker.UpdateWorkers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.TheCooker.SearchToolBar.ApiService.ApiService
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import com.TheCooker.SyncMealsWorker
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class SyncMealsWorkerFactory @Inject constructor(
    private val apiService: ApiService,
    private val recipeRepo: RecipeRepo
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncMealsWorker::class.java.name -> {
                SyncMealsWorker(appContext, workerParameters, apiService, recipeRepo)
            }
            else -> null
        }
    }
}