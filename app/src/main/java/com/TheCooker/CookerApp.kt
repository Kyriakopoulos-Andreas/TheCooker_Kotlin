package com.TheCooker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.TheCooker.UpdateWorkers.SyncMealsWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CookerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: SyncMealsWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}

