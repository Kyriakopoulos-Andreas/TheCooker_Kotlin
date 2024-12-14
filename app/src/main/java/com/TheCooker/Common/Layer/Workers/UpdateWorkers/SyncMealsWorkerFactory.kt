package com.TheCooker.Common.Layer.Workers.UpdateWorkers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.TheCooker.dataLayer.Api.ApiService
import com.TheCooker.Domain.Layer.Repositories.RecipeRepo
import com.TheCooker.SyncMealsWorker
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