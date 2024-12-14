package com.TheCooker.Domain.Layer.Models.LoginModels

import java.io.Serializable

class UserDataModel(
    val userId: String,
    val userName: String?,
    var profilerPictureUrl: String?
): Serializable{
val commonUserId: String = "", // Κοινό αναγνωριστικό χρήστη
val googleUserId: String? = "", // Αναγνωριστικό χρήστη Google
val emailUserId: String? = "", // Αναγνωριστικό χρήστη email/password
val userName: String? = "" ,
var profilerPictureUrl: String? = null,
val email: String? ="",
val password: String? = ""  // Χρησιμοποιείται μόνο για email/password σύνδεση

}