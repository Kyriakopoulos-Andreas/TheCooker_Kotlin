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
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> get() = _emailError

    private val _confirmError = MutableStateFlow<String?>(null)
    val confirmError: StateFlow<String?> get() = _confirmError

    private val _userNameRegError = MutableStateFlow<String?>(null)
    val userNameRegError: StateFlow<String?> get() = _userNameRegError

    private val _passwordRegError = MutableStateFlow<String?>(null)
    val passwordRegError: StateFlow<String?> get() = _passwordRegError





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
    private suspend fun checkEmailExists(email: String): Boolean {
        val trimmedEmail = email.trim() // Αφαίρεση κενών από την αρχή και το τέλος
        return try {
            val result = FirebaseAuth.getInstance().fetchSignInMethodsForEmail(trimmedEmail).await()
            println("!!!!!!!!!!!!!!!!!!!!!!! $result")
            // Ελέγχει αν το signInMethods δεν είναι null και αν η λίστα δεν είναι κενή
            result.signInMethods?.isNotEmpty() == true
        } catch (e: Exception) {
            Log.e("YourViewModel", "Error checking email existence", e)
            false
        }
    }

    fun validateEmail() {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        val email = _Email.value

        if (_Email.value.isNotBlank()) {
            if (!emailRegex.containsMatchIn(email)) {
                _emailError.value = "Email is invalid"
            } else {
                if (_Email.toString().isNotBlank()) {
                    val result = _authResult.value
                    when (result) {
                        is MyResult.Success -> {
                            // Επιτυχής εγγραφή
                        }

                        is MyResult.Error -> {
                            var errorMessage = when (result.exception) {
                                is FirebaseAuthUserCollisionException -> "Η διεύθυνση email χρησιμοποιείται ήδη."
                                is FirebaseNetworkException -> "Σφάλμα σύνδεσης στο διαδίκτυο."
                                // ... άλλες εξαιρέσεις
                                else -> "${result.exception}"
                            }
                            _emailError.value = errorMessage
                        }

                        null -> {
                            _emailError.value = "✔"
                        }
                    }
                }
            }
        } else {
            _emailError.value = "✔"
        }
    }

    fun validConfirmPassword(){
        if(_passwordReg.value != _ConfirmPassReg.value
            && _ConfirmPassReg.value.isNotBlank()
          ){
            _confirmError.value = "Confirm password does not match with password"
        }else if(_passwordReg.value == _ConfirmPassReg.value
            && _ConfirmPassReg.value.isNotBlank()){

            _confirmError.value = "✔"
        }

    }

    fun validRegUsername(){
        if(_userNameRegister.value.isBlank())
        {
            _userNameRegError.value = null
        }
        else if (_userNameRegister.value.length in 1..6
        ) {
            _userNameRegError.value = "Username should be at least 6 characters"
        }
        else{
            _userNameRegError.value = "✔"
        }
    }

    fun validRegPassword(){
        val hasSpecialCharacter =
            Regex("[^a-zA-Z0-9]").containsMatchIn(_passwordReg.value)
        if(_passwordReg.value.isBlank()){
            _passwordRegError.value = null
        }
        else if (_passwordReg.value.length in 1..6
        ) {
            _passwordRegError.value = "Password should be at least 6 characters"

        } else if (!hasSpecialCharacter &&
            _passwordReg.value.length >= 6
        ) {
            _passwordRegError.value = "Password must contain at least one special character"

        }else{
            _passwordRegError.value = "✔"
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


