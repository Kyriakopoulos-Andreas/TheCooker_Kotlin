package com.TheCooker.Domain.Layer.UseCase.SyncMealsFromApiToFirebaseWork

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.TheCooker.dataLayer.Repositories.RecipeRepo
import com.TheCooker.dataLayer.Api.ApiService
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
            SyncMealsFromApiToFirebase::class.java.name -> {
                SyncMealsFromApiToFirebase(appContext, workerParameters, apiService, recipeRepo)
            }
            else -> null
        }
    }
}