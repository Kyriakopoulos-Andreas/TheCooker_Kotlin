package com.TheCooker.Domain.Layer.Models.LoginModels

import java.io.Serializable


data class UserDataModel(
    val uid: String? = "", // Κοινό αναγνωριστικό χρήστη
    val userName: String? = "",
    val googleUserId: String? = "", //
    var profilerPictureUrl: String? = null,
    val email: String? ="",
    val password: String? = ""  // Χρησιμοποιείται μόνο για email/password σύνδεση
) : Serializable