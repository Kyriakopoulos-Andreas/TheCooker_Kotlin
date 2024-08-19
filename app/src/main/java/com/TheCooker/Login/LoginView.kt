package com.TheCooker.Login

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.TheCooker.Login.SignIn.LoginResults
import com.TheCooker.Login.SignIn.SignInState
import com.TheCooker.R
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(viewModel: LoginViewModel,
              onGoogleClick: () -> Unit,
              state: SignInState,
              onLoginButtonClick: () -> Unit
              ) {
    val userName by viewModel.emailLogin
    val password by viewModel.password
    val firstName by viewModel.firstName
    val lastName by viewModel.lastName
    val ConfirmPassReg by viewModel.ConfirmPassReg
    val Email by viewModel.emailRegister
    val passwordReg by viewModel.passwordReg
    var passwordVisible by remember { mutableStateOf(false) }
    var popUpCreatePasword by remember { mutableStateOf(false) }
    var CreatepasswordVisible by remember { mutableStateOf(false) }
    var ConfirmCreatepasswordVisible by remember { mutableStateOf(false) }

    val emailError by viewModel.emailError.collectAsState()
    val passwordRegError by viewModel.passwordRegError.collectAsState()
    val confirmError by viewModel.confirmError.collectAsState()
    val firstNameError by viewModel.firstNameError.collectAsState()
    val lastNameError by viewModel.latsNameError.collectAsState()

    val firstNameBool by viewModel.firstNameBool.collectAsState()
    val lastNameBool by viewModel.firstNameBool.collectAsState()
    val passwordRegBool by viewModel.passwordRegBool.collectAsState()
    val confirmPasswordRegBool by viewModel.confirmPasswordRegBool.collectAsState()
    val emailBool by viewModel.emailBool.collectAsState()



    var isPasswordFocused by remember { mutableStateOf(false) }
    var isConfirmPasswordFocused by remember { mutableStateOf(false) }
    var isFirstNameFocused by remember { mutableStateOf(true) }
    var isLastNameFocused by remember { mutableStateOf(true) }
    var isEmailFocused by remember { mutableStateOf(false) }


    val loginAuthResult by viewModel.authLoginResult.observeAsState()

    val userData by viewModel.userData.observeAsState()



    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError){
        println("!!!!!!!!!!!!${state.signInError}")

        val exception = state.signInError
        exception?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(loginAuthResult, userData) {
        when (loginAuthResult) {
            is LoginResults.Success<*> -> {
                // Ενημερώνουμε το userData αν είναι διαθέσιμο
                if (userData != null) {
                    // Κάνε την μετάβαση στο MenuView με τα δεδομένα χρήστη
                    onLoginButtonClick()
                }
                Toast.makeText(context, "Welcome Chef!", Toast.LENGTH_SHORT).show()
            }
            is LoginResults.Error -> {
                Toast.makeText(context, "Login failed\nInvalid email or password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Διαχείριση άλλων καταστάσεων αν χρειάζεται
            }
        }
    }





    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Προσθήκη εικόνας φόντου
        Image(
            painter = painterResource(id = R.drawable.test2), // Αλλαγή με το όνομα της εικόνας σου
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Image(
            painter = painterResource(id = R.drawable.logo_white), // Εικόνα λογότυπου
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter) // Τοποθέτηση στο κέντρο της κορυφής
                .size(100.dp)
                .padding(top = 8.dp),
            contentScale = ContentScale.Fit // Διατήρηση αναλογιών
        )

        // Προσθήκη υπόλοιπου UI πάνω από την εικόνα
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { viewModel.onUserNameChange(it) },
                    label = { Text("Email") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xAAFFC107),
                        cursorColor = Color(0xFFFFC107),
                        focusedLabelColor = Color(0xFFFFC107),
                        unfocusedLabelColor = Color(0xFFFFC107),
                        focusedTextColor = Color(0xFFFFC107),
                        unfocusedTextColor = Color(0xAAFFC107),
                        containerColor = Color.Transparent
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xAAFFC107),
                        cursorColor = Color(0xFFFFC107),
                        focusedLabelColor = Color(0xFFFFC107),
                        unfocusedLabelColor = Color(0xFFFFC107),
                        focusedTrailingIconColor = Color(0xFFFFC107),
                        unfocusedTrailingIconColor = Color(0xFFFFC107),
                        focusedTextColor = Color(0xFFFFC107),
                        unfocusedTextColor = Color(0xAAFFC107),
                        containerColor = Color.Transparent
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Edit else Icons.Filled.Edit,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(
                    onClick = {viewModel.login(userName, password)

                              },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xFFFFC107)
                    )
                ) {
                    Text(text = "\t\tLogin\t\t")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Box {
                    Text(
                        text = "Create Account",
                        color = Color(0xFFFFC107),
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            popUpCreatePasword = true
                        }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Box {
                    Text(
                        text = "Forgot my password",
                        color = Color(0xFFFFC107),
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { "" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Προσθήκη του Google Icon Button στο τέλος της Column
            IconButton(onClick =  {onGoogleClick()} ,
                modifier = Modifier.size(48.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.google_sign2),
                    contentDescription = "Google Sign-In",
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop

                )
            }
        }
    }


    if (popUpCreatePasword) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onUserNameChange("")
                viewModel.onConfirmPassRegChange("")
                viewModel.onEmailChange("")
                viewModel.onPasswordRegChange("")
                popUpCreatePasword = false
            },
            containerColor = Color(0xFF292929),
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)

                    ) {
                        Text(
                            text = "Create new Password",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFFFFC107)
                        )

                    }
                    Spacer(modifier = Modifier.height(64.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = {
                                viewModel.onFirstNameRegisterChange(it)

                            },
                            singleLine = true,
                            label = {
                                Text(
                                    text = "First Name",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight(300)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),

                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xAAFFC107),
                                cursorColor = Color(0xFFFFC107),
                                focusedLabelColor = Color(0xFFFFC107),
                                unfocusedLabelColor = Color(0xFFFFC107),
                                focusedTextColor = Color(0xFFFFC107),
                                unfocusedTextColor = Color(0xAAFFC107),
                                containerColor = Color.Transparent


                            ),
                           modifier = Modifier.onFocusChanged { focusState: FocusState ->
                               isFirstNameFocused = focusState.isFocused
                                if (!isFirstNameFocused) {
                                    viewModel.validFirstName(false)
                                }
                            }
                        )
                    }

                    firstNameError?.let {
                        val color = if (firstNameError == "✔") Color.Green else Color.Red
                        Text(
                            text = it,
                            style = TextStyle(color = color, fontFamily = FontFamily.Default)
                        )


                    }



                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = {
                                viewModel.onLastNameRegisterChange(it)

                            },
                            singleLine = true,
                            label = {
                                Text(
                                    text = "Last Name",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight(300)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),

                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xAAFFC107),
                                cursorColor = Color(0xFFFFC107),
                                focusedLabelColor = Color(0xFFFFC107),
                                unfocusedLabelColor = Color(0xFFFFC107),
                                focusedTextColor = Color(0xFFFFC107),
                                unfocusedTextColor = Color(0xAAFFC107),
                                containerColor = Color.Transparent


                            ),
                           modifier = Modifier.onFocusChanged { focusState: FocusState ->
                             isLastNameFocused = focusState.isFocused
                             if (!isLastNameFocused) {
                                viewModel.validLastName(false)
                              }
                          }
                        )
                    }

                    lastNameError?.let {
                        val color = if (lastNameError == "✔") Color.Green else Color.Red
                        Text(
                            text = it,
                            style = TextStyle(color = color, fontFamily = FontFamily.Default)
                        )


                    }



                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        OutlinedTextField(
                            value = passwordReg,
                            onValueChange = {
                                viewModel.onPasswordRegChange(it)

                            },
                            singleLine = true,
                            label = {
                                Text(
                                    text = "Password",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight(300)
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xAAFFC107),
                                cursorColor = Color(0xFFFFC107),
                                focusedLabelColor = Color(0xFFFFC107),
                                unfocusedLabelColor = Color(0xFFFFC107),
                                focusedTrailingIconColor = Color(0xFFFFC107),
                                unfocusedTrailingIconColor = Color(0xFFFFC107),
                                focusedTextColor = Color(0xFFFFC107),
                                unfocusedTextColor = Color(0xAAFFC107),
                                containerColor = Color.Transparent


                            ),
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    CreatepasswordVisible = !CreatepasswordVisible
                                }) {
                                    Icon(
                                        imageVector = if (CreatepasswordVisible) Icons.Filled.Edit else Icons.Filled.Edit,
                                        contentDescription = if (CreatepasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (CreatepasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.onFocusChanged { focusState: FocusState ->
                                isPasswordFocused = focusState.isFocused
                                if (!isPasswordFocused) {
                                    viewModel.validRegPassword(false)
                                }


                            }
                        )
                    }
                    passwordRegError?.let {
                        val color = if (passwordRegError == "✔") Color.Green else Color.Red
                        Text(
                            text = it,
                            style = TextStyle(color = color, fontFamily = FontFamily.Default)
                        )

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        OutlinedTextField(
                            value = ConfirmPassReg,
                            onValueChange = {
                                viewModel.onConfirmPassRegChange(it)
                            },
                            singleLine = true,
                            label = {
                                Text(
                                    text = "Confirm Password",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight(300)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    ConfirmCreatepasswordVisible = !ConfirmCreatepasswordVisible
                                }) {
                                    Icon(
                                        imageVector = if (ConfirmCreatepasswordVisible) Icons.Filled.Edit else Icons.Filled.Edit,
                                        contentDescription = if (ConfirmCreatepasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xAAFFC107),
                                cursorColor = Color(0xFFFFC107),
                                focusedLabelColor = Color(0xFFFFC107),
                                unfocusedLabelColor = Color(0xFFFFC107),
                                focusedTrailingIconColor = Color(0xFFFFC107),
                                unfocusedTrailingIconColor = Color(0xFFFFC107),
                                focusedTextColor = Color(0xFFFFC107),
                                unfocusedTextColor = Color(0xAAFFC107),
                                containerColor = Color.Transparent


                            ),
                            visualTransformation = if (ConfirmCreatepasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.onFocusChanged { focus ->
                                isConfirmPasswordFocused = focus.isFocused
                                if (!isPasswordFocused) {
                                    viewModel.validConfirmPassword(false)
                                }

                            }
                        )

                    }
                    confirmError?.let {
                        val color = if (confirmError == "✔") Color.Green else Color.Red

                        Text(
                            text = it,
                            style = TextStyle(color = color, fontFamily = FontFamily.Default)
                        )
                    }







                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        OutlinedTextField(
                            value = Email, // Χρησιμοποιήστε το Email.value
                            onValueChange = { email ->
                                viewModel.onEmailChange(email) // Ενημερώστε το ViewModel


                            },
                            singleLine = true,
                            label = {
                                Text(
                                    text = "Email",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight(300)
                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color(0xFFFFC107),
                                unfocusedBorderColor = Color(0xAAFFC107),
                                cursorColor = Color(0xFFFFC107),
                                focusedLabelColor = Color(0xFFFFC107),
                                unfocusedLabelColor = Color(0xFFFFC107),
                                focusedTextColor = Color(0xFFFFC107),
                                unfocusedTextColor = Color(0xAAFFC107),
                                containerColor = Color.Transparent
                            ),


                            // Όταν το πεδίο χάνει την εστίαση, κάντε επικύρωση


                        )
                    }

// Εμφάνιση σφάλματος

                    emailError?.let {
                        if (!isEmailFocused) {
                            val color1 = if (emailError == "✔") Color.Green else Color.Red
                            Text(
                                text = it,
                                style = TextStyle(color = color1, fontFamily = FontFamily.Default)
                            )
                        }
                    }






                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                viewModel.createPasswordInit()
                                popUpCreatePasword = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color(0xFFFFC107)
                            )

                        ) {
                            Text(text = "Cancel")
                        }

                        Button(
                            onClick = {
                                viewModel.viewModelScope.launch {
                                    val isEmailValid = viewModel.validateEmail()

                                        viewModel.signUp()
                                        viewModel.validRegPassword(true)
                                        viewModel.validConfirmPassword(true)
                                        viewModel.validFirstName(true)
                                        viewModel.validLastName(true)
                                        isEmailFocused = false
                                        isPasswordFocused = false
                                        isConfirmPasswordFocused = false
                                        isFirstNameFocused = false
                                        isLastNameFocused = false

                                        if (passwordRegBool &&
                                            confirmPasswordRegBool &&
                                            firstNameBool &&
                                            lastNameBool) {
                                            popUpCreatePasword = false
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                viewModel.createPasswordInit()
                                            }, 100) // Μικρό delay 100ms

                                    } else {
                                        Log.d("ButtonClick", "Email validation failed")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color(0xFFFFC107)
                            )
                        ) {
                            Text(text = "Create")
                        }

                    }
                }
            }
        )
        }
}

/*if(viewModel.authResult.value != null )
                                    emailError = viewModel.authResult.value.toString()
                                } */