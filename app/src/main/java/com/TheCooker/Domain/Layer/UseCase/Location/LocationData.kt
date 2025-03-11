package com.TheCooker.Domain.Layer.UseCase.Location

data class LocationData(
    val latitude: Double?,
    val longitude: Double?,
    val country: String? = null,
    val city: String? = null,
    val address: String? = null

)
