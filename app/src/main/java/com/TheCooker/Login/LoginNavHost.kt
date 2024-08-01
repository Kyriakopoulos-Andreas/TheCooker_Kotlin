package com.TheCooker.Login

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.TheCooker.Login.Authentication.GoogleAuth.GoogleClient
import com.TheCooker.Login.Authentication.GoogleAuth.UserData
import com.TheCooker.Login.CrPassword.MyResult
import com.TheCooker.Menu.MenuView
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginNavigator(viewModel: LoginViewModel, client: GoogleClient) {
    val navController2 = rememberNavController()

    val context = LocalContext.current








    // Χρησιμοποιούμε rememberCoroutineScope για την εκτέλεση εργασιών σε Coroutine
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController2, startDestination = "LoginView") {
        composable(route = "LoginView") {

            val state by viewModel.state.collectAsStateWithLifecycle()

            // Δημιουργία launcher για ActivityResult
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                   coroutineScope.launch {
                       val signInResult = client.signInWithIntent(
                           intent = result.data ?: return@launch
                       )
                       viewModel.onSignInResult(signInResult)
                   }
                }
            }

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if(state.isSignInSuccessful){
                    Toast.makeText(
                        context,
                        "Sign in successful",
                        Toast.LENGTH_LONG
                    ).show()

                    navController2.currentBackStackEntry?.savedStateHandle?.set("User", client.getSingedInUser())
                    navController2.navigate("MenuView")
                    viewModel.resetState()


                }


            }


            LoginView(
                viewModel = viewModel,
                state = state,
                onGoogleClick = {
                    coroutineScope.launch {
                        val signInIntentSender = client.signIn(context)

                        signInIntentSender?.let {
                            val intentSenderRequest = IntentSenderRequest.Builder(it).build()
                            launcher.launch(intentSenderRequest)
                        } ?: run {
                            Toast.makeText(
                                context,
                                "Sign in failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                onLoginButtonClick = {
                    navController2.navigate("MenuView"){
                        popUpTo("LoginView"){
                            inclusive = true
                        }
                    }

                }

            )
        }
        composable(route = "MenuView") {
            val user = navController2.previousBackStackEntry?.savedStateHandle?.get<UserData>("User")

                MenuView(user, viewModel.userData.value)

        }

}
}
