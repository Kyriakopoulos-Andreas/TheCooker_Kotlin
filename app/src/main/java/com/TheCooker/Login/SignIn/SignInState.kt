package com.TheCooker.Login.SignIn

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
