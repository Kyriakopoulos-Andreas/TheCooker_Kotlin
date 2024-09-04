package com.TheCooker.Login.SignIn

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataProvider@Inject constructor() {
    var userData: UserData? = null
}