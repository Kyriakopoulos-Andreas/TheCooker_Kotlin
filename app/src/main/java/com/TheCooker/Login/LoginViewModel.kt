package com.TheCooker.Login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.TheCooker.Login.SignIn.SignInState

import com.TheCooker.Login.CrPassword.UserRepo
import com.TheCooker.Login.SignIn.CreateResults
import com.TheCooker.Login.SignIn.LoginResults
import com.TheCooker.Login.SignIn.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
  private val _userRepo: UserRepo
) : ViewModel() {


    var userRepo: UserRepo = _userRepo



    //Crate Handle
    private val _authCreateResult = MutableLiveData<CreateResults<Boolean>>()
    val authCreateResult: LiveData<CreateResults<Boolean>> get() = _authCreateResult

    private val _authLoginResult = MutableLiveData<LoginResults<Boolean>>()
    val authLoginResult: LiveData<LoginResults<Boolean>> get() = _authLoginResult

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData




    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> get() = _emailError
    private val _confirmError = MutableStateFlow<String?>(null)
    val confirmError: StateFlow<String?> get() = _confirmError
    private val _firstNameError = MutableStateFlow<String?>(null)
    val firstNameError: StateFlow<String?> get() = _firstNameError
    private val _lastNameError = MutableStateFlow<String?>(null)
    val latsNameError: StateFlow<String?> get() = _lastNameError
    private val _passwordRegError = MutableStateFlow<String?>(null)
    val passwordRegError: StateFlow<String?> get() = _passwordRegError

    private val _firstNameBool = MutableStateFlow<Boolean>(false)
    val firstNameBool: StateFlow<Boolean> get() = _firstNameBool
    private val _lastNameBool = MutableStateFlow<Boolean>(false)
    val lastNameBool: StateFlow<Boolean> get() = _lastNameBool
    private val _passwordRegBool = MutableStateFlow<Boolean>(false)
    val passwordRegBool: StateFlow<Boolean> get() = _passwordRegBool
    private val _confirmPasswordRegBool = MutableStateFlow<Boolean>(false)
    val confirmPasswordRegBool: StateFlow<Boolean> get() = _confirmPasswordRegBool

    private val _emailBool = MutableStateFlow<Boolean>(false)
    val emailBool: StateFlow<Boolean> get() = _emailBool


    private val _emailLogin = mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _firstName = mutableStateOf("")
    private val _lastName = mutableStateOf("")
    private val _confirmPassReg = mutableStateOf("")
    private val _emailRegister = mutableStateOf("")
    private val _passwordReg = mutableStateOf("")

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()


    val emailLogin: State<String> = _emailLogin
    val password: State<String> = _password
    val firstName: State<String> = _firstName
    val lastName: State<String> = _lastName
    val ConfirmPassReg: State<String> = _confirmPassReg
    val emailRegister: State<String> = _emailRegister
    val passwordReg: State<String> = _passwordReg

    private val _isDialogVisible =mutableStateOf(false)
    val isDialogVisible: State<Boolean> = _isDialogVisible




    fun showDialog() {
        _isDialogVisible.value = true
    }

    fun hideDialog() {
        _isDialogVisible.value = false
    }

    fun loginInit(){
        _emailLogin.value = ""
        _password.value = ""

    }




    fun createPasswordInit() {
        _emailError.value = null
        _firstNameError.value = ""
        _lastNameError.value = ""
        _passwordRegError.value = ""
        _confirmError.value = ""
        _emailBool.value = false
        _passwordRegBool.value = false
        _firstNameBool.value = false
        _lastNameBool.value = false
        _confirmPasswordRegBool.value = false
        _firstName.value = ""
        _lastName.value = ""
        _confirmPassReg.value = ""
        _passwordReg.value = ""
        _emailRegister.value = ""


    }

    fun onUserNameChange(newUserName: String) {
        _emailLogin.value = newUserName
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onFirstNameRegisterChange(firstName: String) {
        _firstName.value = firstName
    }

    fun onLastNameRegisterChange(lastName: String) {
        _lastName.value = lastName
    }

    fun onConfirmPassRegChange(newConfirmPassReg: String) {
        _confirmPassReg.value = newConfirmPassReg
    }

    fun onEmailChange(newEmail: String) {
        _emailRegister.value = newEmail


    }

    fun onPasswordRegChange(newPasswordReg: String) {
        _passwordReg.value = newPasswordReg
    }


    fun  signUp(){
        viewModelScope.launch {
            Log.d("LoginViewModel", "signUp called")
            Log.d(
                "LoginViewModel",
                "email: ${_emailRegister.value}, password: ${_passwordReg.value}, userName: ${_firstName.value}"
            )

            // Επικύρωση των στοιχείων πριν την εγγραφή

            if (_passwordRegBool.value && _confirmPasswordRegBool.value && _firstNameBool.value && _lastNameBool.value
            ) {
                val result = _userRepo.signUp(
                    firstName = _firstName.value,
                    password = _passwordReg.value,
                    email = _emailRegister.value,
                    lastName = _lastName.value,
                    profilePictureUrl = ""
                )
                _authCreateResult.value = result


            }
        }
    }


    suspend fun validateEmail(): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        val email = _emailRegister.value

        return withContext(Dispatchers.IO) {
            val userExists = _userRepo.checkIfUserExistsInFirestore(email)

            if (email.isNotBlank()) {
                if (!emailRegex.containsMatchIn(email)) {
                    _emailError.value = "Email is invalid"
                    _emailBool.value = false
                    false // επιστρέφουμε false εδώ
                } else if(userExists) {
                    _emailError.value = "Email already exists"
                    _emailBool.value = false
                    false // επιστρέφουμε false εδώ
                } else {
                    _emailError.value = "✔"
                    _emailBool.value = true
                    true // επιστρέφουμε true εδώ
                }
            } else {
                _emailError.value = "Email cannot be empty"
                _emailBool.value = false
                false // επιστρέφουμε false εδώ
            }
        }
    }



    fun validConfirmPassword(createRequest: Boolean) {
        if (_passwordReg.value.isBlank()) {
            _confirmError.value = null
            _confirmPasswordRegBool.value = false
            if (_passwordReg.value.isBlank() && createRequest) {
                _confirmError.value = "Confirm password cannot be empty"
            }
        } else if (_passwordReg.value != _confirmPassReg.value
            && _confirmPassReg.value.isNotBlank()
        ) {
            _confirmError.value = "Confirm password does not match with password"
            _confirmPasswordRegBool.value = false
        } else if (_passwordReg.value == _confirmPassReg.value
            && _confirmPassReg.value.isNotBlank()
        ) {

            _confirmError.value = "✔"
            _confirmPasswordRegBool.value = true
        }

    }


    fun validFirstName(createRequest: Boolean) {
        if (_firstName.value.isBlank()) {
            _firstNameError.value = null
            _firstNameBool.value = false
            if (_firstName.value.isBlank() && createRequest) {
                _firstNameError.value = "First Name cannot be empty"
                _firstNameBool.value = false
            }
        } else if (_firstName.value.length in 1..2
        ) {
            _firstNameError.value = "First Name should be at least 2 characters"
            _firstNameBool.value = false
        } else {
            _firstNameError.value = "✔"
            _firstNameBool.value = true
        }
    }

    fun validLastName(createRequest: Boolean) {
        if (_lastName.value.isBlank()) {
            _lastNameError.value = null
            _lastNameBool.value = false
            if (_lastName.value.isBlank() && createRequest) {
                _lastNameError.value = "Last Name cannot be empty"
                _lastNameBool.value = false
            }
        } else if (_lastName.value.length in 1..2
        ) {
            _lastNameError.value = "First Name should be at least 2 characters"
            _lastNameBool.value = false
        } else {
            _lastNameError.value = "✔"
            _lastNameBool.value = true
        }
    }

    fun validRegPassword(createRequest: Boolean) {
        val hasSpecialCharacter =
            Regex("[^a-zA-Z0-9]").containsMatchIn(_passwordReg.value)
        if (_passwordReg.value.isBlank()) {
            _passwordRegError.value = null
            _passwordRegBool.value = false
            if (_passwordReg.value.isBlank() && createRequest) {
                _passwordRegError.value = "Password cannot be empty"
                _passwordRegBool.value = false
            }
        } else if (_passwordReg.value.length in 1..5
        ) {
            _passwordRegError.value = "Password should be at least 6 characters"
            _passwordRegBool.value = false

        } else if (!hasSpecialCharacter &&
            _passwordReg.value.length >= 6
        ) {
            _passwordRegError.value = "Password must contain at least one special character"
            _passwordRegBool.value = false

        } else if (_passwordReg.value.isBlank()) {
            _passwordRegError.value = "Password cannot be empty"
        } else {
            _passwordRegError.value = "✔"
            _passwordRegBool.value = true

        }

    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authLoginResult.value = _userRepo.login(email, password)
            println("Login result: ${_userRepo.login(email, password)}")
            if (_authLoginResult.value is LoginResults.Success) {
                val currentUser = _userRepo.auth.currentUser
                println("Current userooooooooooo: $currentUser")
                if (currentUser != null) {
                    val userDetails = _userRepo.getUserDetails(currentUser.email ?: "")
                    println("User detailskkkkkkkk: $userDetails")
                    if (userDetails is LoginResults.Success<*>) {
                        _userData.value = userDetails.data as UserData?
                        println("!!!!!!eeee!!!!!!!!!!!!!!!!!! ${_userData.value}")
                    }
                }
            }
        }
    }

    fun onSignInResult(result: LoginResults<UserData>) {
        _state.update {
            when (result) {
                is LoginResults.Success -> {
                    // Ενημερώνουμε την κατάσταση ως επιτυχία και δεν εμφανίζουμε σφάλμα
                    it.copy(
                        isSignInSuccessful = true,
                        signInError = null
                    )
                }
                is LoginResults.Error -> {
                    // Ενημερώνουμε την κατάσταση με το μήνυμα σφάλματος
                    it.copy(
                        isSignInSuccessful = false,
                        signInError = result.exception?.message
                    )
                }
            }
        }
    }


    fun resetState() {
        _state.update { SignInState() }
    }
    fun logout() {
        _authLoginResult.value = LoginResults.Success(false)
    }


    }




/*   suspend fun checkEmailExists(): Boolean {
       val trimmedEmail = _Email.value.trim() // Αφαίρεση κενών από την αρχή και το τέλος
       return try {
           val result = FirebaseAuth.getInstance().fetchSignInMethodsForEmail(trimmedEmail).await()

           // Εξαγωγή της λίστας signInMethods
           val signInMethods = result.signInMethods
           if (signInMethods != null) {
               // Εκτύπωση της λίστας στην κονσόλα
               println("Sign-in methods for email $trimmedEmail: $signInMethods")

               // Για Android Logcat
               Log.d("YourViewModel", "Sign-in methods for email $trimmedEmail: $signInMethods")
           } else {
               println("No sign-in methods found for email $trimmedEmail.")
               Log.d("YourViewModel", "No sign-in methods found for email $trimmedEmail.")
           }

           // Ελέγχει αν το signInMethods δεν είναι null και αν η λίστα δεν είναι κενή
           signInMethods?.isNotEmpty() == true

       } catch (e: Exception) {
           Log.e("YourViewModel", "Error checking email existence", e)
           false
       }
   } */

