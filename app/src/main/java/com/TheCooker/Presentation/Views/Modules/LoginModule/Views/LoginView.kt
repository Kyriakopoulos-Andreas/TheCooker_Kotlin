package com.TheCooker.Presentation.Views.Modules.LoginModule.Views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.TheCooker.Presentation.Views.Modules.LoginModule.ViewModels.LoginViewModel
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Common.Layer.Resources.SignInState
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
    var passwordVisible by remember { mutableStateOf(false) }
    val popUpCreatePassword = viewModel.isDialogVisible
    val scope = rememberCoroutineScope()



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
        val authResult = loginAuthResult // Αποθηκεύουμε το loginAuthResult σε μια τοπική μεταβλητή

        when (authResult) {
            is LoginResults.Success<*> -> {
                // Ελέγχουμε αν το αποτέλεσμα είναι `true` και αν υπάρχουν διαθέσιμα δεδομένα χρήστη
                val isLoggedIn = authResult.data as? Boolean ?: true
                if (isLoggedIn && userData != null) {
                    // Κάνε την μετάβαση στο MenuView με τα δεδομένα χρήστη
                    onLoginButtonClick()
                    println(userData)
                    Toast.makeText(context, "Welcome Chef!", Toast.LENGTH_SHORT).show()
                }
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
                    onClick = {scope.launch{viewModel.login(userName, password)}

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
                            viewModel.showDialog()
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


    if (popUpCreatePassword.value) {
        CrPasswordView(viewModel)

        }
}

/*if(viewModel.authResult.value != null )
                                    emailError = viewModel.authResult.value.toString()
                                } */