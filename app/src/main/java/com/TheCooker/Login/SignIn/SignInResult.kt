package com.TheCooker.Login.SignIn

import java.io.Serializable


sealed class CreateResults<out T>{
    data class Success <out T>(val data: T): CreateResults<T>()
    data class Error(val exception: Exception?): CreateResults<Nothing>()

}

sealed class LoginResults<out T>{


    data class Success <out T>(val data: T): LoginResults<T>()
    data class Error(val exception: Exception?): LoginResults<Nothing>()

}




data class UserData(
    val uid: String? = "", // Κοινό αναγνωριστικό χρήστη
    val userName: String? = "",
    val googleUserId: String? = "", //
    var profilerPictureUrl: String? = null,
    val email: String? ="",
    val password: String? = ""  // Χρησιμοποιείται μόνο για email/password σύνδεση
) : Serializable
