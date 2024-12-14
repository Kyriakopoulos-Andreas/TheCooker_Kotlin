package com.TheCooker.Presentation.Views.Modules.DrawerModule.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.AlertDialog
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.TheCooker.Domain.Layer.UseCase.GoogleClient
import com.TheCooker.Presentation.Views.Modules.LoginModule.ViewModels.LoginViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LogOutAlertDialog(
    dialogOpen: MutableState<Boolean>,
    googleClient: GoogleClient,
    navController: NavHostController,
    loginViewModel: LoginViewModel
){

    if (dialogOpen.value) {
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = {
                dialogOpen.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    dialogOpen.value = false
                    loginViewModel.logout()
                    loginViewModel.loginInit()


                    scope.launch {

                        navController.navigate("LoginView") {
                        }

                        googleClient.signOut()

                    }
                    //viewModel.signOut()
                    //navController.navigate("Login")
                    //}
                }) {
                    Text(text = "Confirm",
                        color = Color(0xFFFFC107))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogOpen.value = false
                }) {
                    Text(text = "Cancel",
                        color = Color(0xFFFFC107))
                }
            },
            title = {
                Text(text = "Log out",
                    color = Color.White,
                    style = MaterialTheme.typography.h6,
                    fontFamily = FontFamily.Monospace)
            },
            text = {
                Text(text = "Are you sure you want to log out?",
                    color = Color.White,
                    style = MaterialTheme.typography.h6,
                    fontFamily = FontFamily.Monospace
                   )

            },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFFC107))
                .padding(8.dp),
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color(0xFF202020),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )

        )
    }
}