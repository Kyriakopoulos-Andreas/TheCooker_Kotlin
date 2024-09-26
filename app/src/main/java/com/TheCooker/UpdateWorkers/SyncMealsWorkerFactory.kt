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
    private val apiService: ApiService, //Κάνουμε Inject τον constructor Και περνάμε τις εξαρτήσεις με Hilt!!!!
    private val recipeRepo: RecipeRepo
) : WorkerFactory() {

    override fun createWorker( // Υπερφόρτωση του createWorker με τις εξαρτήσεις που χρειαζόμαστε στον SyncMealsWorker
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) { // Αν το worker name είναι SyncMealsWorker δημιούργησε instance του worker αλλιώς επέστρεψε null
            SyncMealsWorker::class.java.name -> {
                SyncMealsWorker(appContext, workerParameters, apiService, recipeRepo)
            }
            else -> null
        }
    }
}