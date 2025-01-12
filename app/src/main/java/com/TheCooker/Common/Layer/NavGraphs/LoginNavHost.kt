package com.TheCooker.Common.Layer.NavGraphs

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.TheCooker.Domain.Layer.UseCase.GoogleIntents.GoogleClient
import com.TheCooker.Presentation.Views.Modules.LoginModule.Views.LoginView
import com.TheCooker.Presentation.Views.Modules.LoginModule.ViewModels.LoginViewModel
import com.TheCooker.Presentation.Views.Modules.TopBarViews.MainTopBarViewSupport
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel


import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginNavigator(viewModel: LoginViewModel,
                   client: GoogleClient,
                   createMealViewModel: CreateMealViewModel,
                   mealsDetailViewModel: MealsDetailViewModel,
                   mealsViewModel: MealsViewModel,
                   categoryViewModel: CategoryViewModel
) {
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
                            "Welcome Chef",
                            Toast.LENGTH_LONG
                        ).show()

                          //TODO
//                        client.getSignedInUser()?.let { it1 -> viewModel.setUserData(it1) } // I choose to keep the user data in the viewModel and not to the backStack

                        navController2.navigate("MenuView"){
                            popUpTo("LoginView") { inclusive = true }
                        }
                        viewModel.resetState() // Επαναφερει το state isSignInSuccessful σε false ετσι ωστε να μπορει να γινει Log out




                    }


                }


                LoginView(
                    viewModel = viewModel,
                    state = state,
                    onGoogleClick = {
                        coroutineScope.launch {
                            val signInIntentSender = client.signIn()

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
                            popUpTo("LoginView") { inclusive = true }
                        }
                    }
                )
            }
            composable(route = "MenuView") {
                val user = viewModel.userData.value   // Get the user from googleSignIn using the viewModel and not the backStack
                //val user = navController2.previousBackStackEntry?.savedStateHandle?.get<UserDataModel>("User")
                    MainTopBarViewSupport(viewModel.userData.value ?: user, client, navController2, loginViewModel = viewModel, createMealViewModel = createMealViewModel, mealsDetailViewModel = mealsDetailViewModel, mealsViewModel = mealsViewModel, categoryViewModel = categoryViewModel )

            }

}
}


