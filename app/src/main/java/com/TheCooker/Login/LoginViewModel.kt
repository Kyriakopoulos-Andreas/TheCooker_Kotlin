package com.TheCooker.Login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.TheCooker.Login.Authentication.SignInResult
import com.TheCooker.Login.Authentication.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel: ViewModel() {
    private val _userName = mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _userNameRegister = mutableStateOf("")
    private val _ConfirmPassReg = mutableStateOf("")
    private val _Email = mutableStateOf("")
    private val _passwordReg = mutableStateOf("")

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()




    val userName: State<String> = _userName
    val password: State<String> = _password
    val userNameRegister: State<String> = _userNameRegister
    val ConfirmPassReg: State<String> = _ConfirmPassReg
    val Email: State<String> = _Email
    val passwordReg: State<String> = _passwordReg

    fun onUserNameChange(newUserName: String) {
        _userName.value = newUserName
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onUserNameRegisterChange(newUserNameRegister: String) {
        _userNameRegister.value = newUserNameRegister
    }

    fun onConfirmPassRegChange(newConfirmPassReg: String) {
        _ConfirmPassReg.value = newConfirmPassReg
    }

    fun onEmailChange(newEmail: String) {
        _Email.value = newEmail
    }

    fun onPasswordRegChange(newPasswordReg: String) {
        _passwordReg.value = newPasswordReg
    }

    fun onSignInResult(result: SignInResult){
        _state.update { it.copy(
            isSignInSuccessful = true,
            signInError = result.errorMessage
        ) }
    }

    fun resetState(){
        _state.update { SignInState() }
    }



}