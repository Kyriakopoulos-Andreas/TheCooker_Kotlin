package com.TheCooker

import com.TheCooker.Presentation.Views.Modules.theme.TheCookerTheme


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.TheCooker.Domain.Layer.UseCase.GoogleIntents.GoogleClient
import com.TheCooker.Common.Layer.NavGraphs.LoginNavigator
import com.TheCooker.Presentation.Views.Modules.LoginModule.ViewModels.LoginViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Domain.Layer.UseCase.SyncMealsFromApiToFirebaseWork.scheduleMonthlySync
import com.TheCooker.Presentation.Views.Modules.SharedModule.SplashScreenViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


private val viewModel: SplashScreenViewModel by viewModels()

    @Inject
    lateinit var googleClient: GoogleClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { false }



        scheduleMonthlySync(this)
        setContent {
            val focusManager = LocalFocusManager.current

            TheCookerTheme {
                val categoryViewModel = hiltViewModel<CategoryViewModel>()
                val mealsDetailViewModel = hiltViewModel<MealsDetailViewModel>()
                val mealsViewModel = hiltViewModel<MealsViewModel>()
                val loginViewModel = hiltViewModel<LoginViewModel>()
                val createMealViewModel = hiltViewModel<CreateMealViewModel>()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            focusManager.clearFocus()
                        },
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginNavigator(
                        loginViewModel,
                        googleClient,
                        createMealViewModel,
                        mealsDetailViewModel,
                        mealsViewModel,
                        categoryViewModel
                    )
                }
                }
            }
        }
    }