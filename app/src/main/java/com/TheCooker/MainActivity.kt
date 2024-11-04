package com.TheCooker

import android.content.Context
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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.TheCooker.Login.Authentication.GoogleAuth.GoogleClient

import com.TheCooker.NavGraphs.LoginNavigator
import com.TheCooker.Login.LoginViewModel
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
import com.TheCooker.SearchToolBar.ViewModels.CreateMealViewModel
import com.TheCooker.SearchToolBar.ViewModels.MealsDetailViewModel
import com.TheCooker.SearchToolBar.ViewModels.MealsViewModel

import com.TheCooker.SearchToolBar.ViewModels.SearchCategoryViewModel
import com.TheCooker.UpdateWorkers.scheduleMonthlySync
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {




    @Inject
    lateinit var googleClient: GoogleClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Κάνω sync σε μια προκαθορισμένη ημερομηνία
        scheduleMonthlySync(this)
        setContent {




            // Ορίζουμε το ViewModelStore για τη διαχείριση των ViewModels
            TheCookerTheme {

                    val searchCategoryViewModel = hiltViewModel<SearchCategoryViewModel>()
                val mealsDetailViewModel = hiltViewModel<MealsDetailViewModel>()
                val mealsViewModel = hiltViewModel<MealsViewModel>()
                val loginViewModel = hiltViewModel<LoginViewModel>()
                val createMealViewModel = hiltViewModel<CreateMealViewModel>()


                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {

                        LoginNavigator(loginViewModel, googleClient, createMealViewModel, mealsDetailViewModel)
                        println("Hello")


                    }
                }
            }
        }
    }