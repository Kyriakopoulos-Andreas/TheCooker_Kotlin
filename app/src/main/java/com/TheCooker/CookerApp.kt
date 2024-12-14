package com.TheCooker

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.work.Configuration
import com.TheCooker.Common.Layer.Workers.UpdateWorkers.SyncMealsWorkerFactory
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

    companion object {
        const val ADMIN_DEVICE_ID = "admin_device_id"
        lateinit var HARD_CODED_ADMIN_ID: String
    }

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()
        HARD_CODED_ADMIN_ID = getString(R.string.Pixel6DeviceID)


        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        Log.i("DeviceCheck", "Current device ID: $currentDeviceId")

        // Αποθήκευση του Device ID του admin μόνο αν δεν υπάρχει
        if (!sharedPreferences.contains(HARD_CODED_ADMIN_ID)) {
            // Αποθήκευση του hardcoded admin ID
            sharedPreferences.edit().putString(ADMIN_DEVICE_ID, HARD_CODED_ADMIN_ID).apply()
            Log.d("DeviceCheck", "Stored hardcoded admin device ID: $HARD_CODED_ADMIN_ID")
        }

        // Πάρε το adminDeviceId από τα SharedPreferences
        val adminDeviceId = sharedPreferences.getString(ADMIN_DEVICE_ID, null)
        Log.d("DeviceCheck", "Admin device ID: $adminDeviceId")

        // Ελενξε αν το τρέχον Device ID είναι το admin
        if (currentDeviceId == adminDeviceId) {
            Log.d("DeviceCheck", "The current device is the admin device.")
        } else {
            Log.d("DeviceCheck", "The current device is NOT the admin device.")
        }
    }
}
