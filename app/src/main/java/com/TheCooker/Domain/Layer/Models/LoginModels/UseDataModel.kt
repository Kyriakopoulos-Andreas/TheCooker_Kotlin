package com.TheCooker.Domain.Layer.Models.LoginModels

import java.io.Serializable

data class UserDataModel(
    val uid: String? = "",
    val userName: String? = "",
    val googleUserId: String? = "", //
    var profilePictureUrl: String? = null,
    var backGroundPictureUrl: String? = null,
    val email: String? ="",
    val password: String? = "",
    var city: String? = "",
    var country: String? = "",
    var specialties: String? = "",
    var chefLevel: String? = "",
    var goldenChefHats: Int? = 0,
    var countryFromWhichUserConnected: String? = null,
    var cityFromWhichUserConnected: String? = null,
    var connectedAddress: String? = null,
) : Serializable
