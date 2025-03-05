package com.TheCooker.Domain.Layer.UseCase.Location.Permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.TheCooker.Domain.Layer.UseCase.Location.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationUtils {

    private lateinit var  locationClient: FusedLocationProviderClient // FusedLocationProviderClient is google api that uses wifi device sensors, GPS(NOT HERE--needs fine location permission) AND Mobile Network to provide location data

    fun init(context: Context) {
        locationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun requestLocationUpdates(context: Context, callback: (LocationData) -> Unit) {

        if (!hasLocationPermission(context)) {
            println("Permission denied: Cannot access location")
            return
        }

        if (!::locationClient.isInitialized) {
            throw IllegalStateException("LocationUtils not initialized. Call init(context) first.")
        }

        try {
            locationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        callback(LocationData(location.latitude, location.longitude))
                    } else {
                        println("Location is null")
                    }
                }
                .addOnFailureListener {
                    println("Failed to get location: ${it.message}")
                }
        } catch (e: SecurityException) {
            println("SecurityException: ${e.message}")
        }
    }


    fun hasLocationPermission(context: Context): Boolean {

        return ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
