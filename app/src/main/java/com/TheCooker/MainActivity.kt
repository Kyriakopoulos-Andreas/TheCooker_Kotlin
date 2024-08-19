package com.TheCooker

import com.TheCooker.ui.theme.TheCookerTheme


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.TheCooker.Login.Authentication.GoogleAuth.GoogleClient

import com.TheCooker.NavGraphs.LoginNavigator
import com.TheCooker.Login.LoginViewModel
import com.google.android.gms.auth.api.identity.Identity


class MainActivity : ComponentActivity() {

    private val googleClient by lazy {
        GoogleClient(
            context = applicationContext,
            client = Identity.getSignInClient(applicationContext)
        )
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModelLogin: LoginViewModel by viewModels()


            // Ορίζουμε το ViewModelStore για τη διαχείριση των ViewModels
            TheCookerTheme {

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        LoginNavigator(viewModelLogin, googleClient)
                        println("Hello")


                    }
                }
            }
        }
    }