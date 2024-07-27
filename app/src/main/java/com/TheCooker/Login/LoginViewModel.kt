package com.TheCooker.Login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.Login.Authentication.GoogleAuth.SignInResult
import com.TheCooker.Login.Authentication.GoogleAuth.SignInState
import com.TheCooker.Login.CrPassword.Injection
import com.TheCooker.Login.CrPassword.MyResult
import com.TheCooker.Login.CrPassword.UserRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val userRepo: UserRepo
    init {
        userRepo = UserRepo(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<MyResult<Boolean>>()
    val authResult: LiveData<MyResult<Boolean>> get() = _authResult



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


    fun signUp() {
        viewModelScope.launch {
            Log.d("LoginViewModel", "signUp called")
            Log.d("LoginViewModel", "email: ${_Email.value}, password: ${_passwordReg.value}, userName: ${_userNameRegister.value}")
            val result = userRepo.signUp(
                passwordName = _userNameRegister.value,
                password = _passwordReg.value,
                email = _Email.value
            )
            _authResult.value = result
            Log.d("LoginViewModel", "signUp result: $result")
        }
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