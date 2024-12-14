package com.TheCooker.Domain.Layer.Models.LoginModels

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
