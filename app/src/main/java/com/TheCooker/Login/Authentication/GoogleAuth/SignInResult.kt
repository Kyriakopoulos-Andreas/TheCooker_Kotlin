package com.TheCooker.Login.Authentication.GoogleAuth

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
    val commonUserId: String = "", // Κοινό αναγνωριστικό χρήστη
    val googleUserId: String? = "", // Αναγνωριστικό χρήστη Google
    val emailUserId: String? = "", // Αναγνωριστικό χρήστη email/password
    val userName: String? = "" ,
    var profilerPictureUrl: String? = null,
    val email: String? ="",
    val password: String? = ""  // Χρησιμοποιείται μόνο για email/password σύνδεση
) : Serializable
