package com.TheCooker.Domain.Layer.UseCase.Location

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.UseCase.Location.Permissions.LocationUtils
import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor (
    private val _userRepo: UserRepo,
    val _userDataProvider: UserDataProvider,
    @ApplicationContext private val context: Context // Εδώ παίρνουμε το applicationContext μέσω του Hilt
): ViewModel() {

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location

    init {
        LocationUtils.init(context.applicationContext)
    }

//    fun updateLocation(location: LocationData) {
//        _location.value = location
//    }

    fun requestLocation(context: Context) {
        if (LocationUtils.hasLocationPermission(context)) {
            LocationUtils.requestLocationUpdates(context) { newLocation ->
                _location.value = newLocation
                //_userRepo.uploadLocation(newLocation)
            }
        } else {
            println("Permission not granted, requesting permission first.")
        }
    }

}