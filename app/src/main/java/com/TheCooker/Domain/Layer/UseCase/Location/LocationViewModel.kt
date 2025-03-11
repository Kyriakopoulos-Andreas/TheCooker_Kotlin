package com.TheCooker.Domain.Layer.UseCase.Location

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.UseCase.Location.Permissions.LocationUtils
import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor (
    private val _userRepo: UserRepo,
    val _userDataProvider: UserDataProvider,
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location

    init {
        LocationUtils.init(context.applicationContext)
        viewModelScope.launch {
            requestLocation(context)
        }
       
    }

//    fun updateLocation(location: LocationData) {
//        _location.value = location
//    }

    suspend fun requestLocation(context: Context) {
        if (LocationUtils.hasLocationPermission(context)) {
            LocationUtils.requestLocationUpdates(context) { newLocation ->
                _location.value = newLocation
                Log.d("Location on ViewModel", "New location: ${newLocation.country}")
                Log.d("Location on ViewModel", "New location: ${newLocation.city}")
                viewModelScope.launch {
                    _userRepo.updateUserLocation(newLocation, _userDataProvider.userData!!)

                }
            }
        } else {
           Log.d("Location on ViewModel", "Permission not granted")
        }
    }

}