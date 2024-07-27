package com.TheCooker.Login.Authentication.GoogleAuth

import java.lang.Error

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
