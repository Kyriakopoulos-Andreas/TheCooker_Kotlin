package com.TheCooker.Domain.Layer.UseCase.Location.Permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.TheCooker.Domain.Layer.UseCase.Location.LocationData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import java.net.URLDecoder

import java.util.Locale

object LocationUtils {

    private lateinit var locationClient: FusedLocationProviderClient // FusedLocationProviderClient is Google API that uses Wi-Fi, device sensors, GPS (when needed), and mobile network to provide location data

    fun init(context: Context) {
        locationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun requestLocationUpdates(context: Context, callback: (LocationData) -> Unit) {
        // First, check if the necessary permissions are granted
        if (!hasLocationPermission(context)) {
            Log.d("TestPermissionLocationUtils", "Permission not granted")
            return
        }

        // Initialize locationClient if not already initialized
        if (!::locationClient.isInitialized) {
            throw IllegalStateException("LocationUtils not initialized. Call init(context) first.")
        }



        // Create a LocationRequest with low power usage
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 10000).build()

        try {
            // Try to get the last known location
            val location = locationClient.lastLocation.await()

            if (location != null) {
                // If the last location is available, process it
                callback(reverseGeocodeLocation(context, LocationData(location.latitude, location.longitude)))
                Log.d("TestLocationLocationUtils", "Location is not null")
            } else {
                // If the last known location is null, request updates
                Log.d("TestLocationLocationUtils", "Location is null")

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val newLocation = result.lastLocation
                        if (newLocation != null) {
                            callback(reverseGeocodeLocation(context, LocationData(newLocation.latitude, newLocation.longitude)))
                        } else {
                            Log.d("TestLocationLocationUtils", "Still null location")
                        }
                        locationClient.removeLocationUpdates(this)
                    }
                }

                // Request location updates and await the result
                locationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            }
        } catch (e: SecurityException) {
            Log.d("SecurityExceptionLocation:", e.message.toString())
        }
    }


    // Function to check if location permission is granted
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Function to reverse geocode the location into human-readable address
    fun reverseGeocodeLocation(context: Context, location: LocationData): LocationData {
        val geocoder = Geocoder(context, Locale.getDefault())

        val coordinates = location.latitude?.let { lat ->
            location.longitude?.let { lng ->
                com.google.android.gms.maps.model.LatLng(lat, lng)
            }
        }

        val addresses = coordinates?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }

        return if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val correctedAddress = address.getAddressLine(0)
            val city = address.locality
            val country = address.countryName

            // Return LocationData object with all the details
            LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                country = country,
                city = city,
                address = correctedAddress
            )
        } else {
            // Return LocationData with nulls for the missing details
            LocationData(
                latitude = location.latitude,
                longitude = location.longitude,
                country = "Unknown",
                city = "Unknown",
                address = "Address not found"
            )
        }
    }
}

