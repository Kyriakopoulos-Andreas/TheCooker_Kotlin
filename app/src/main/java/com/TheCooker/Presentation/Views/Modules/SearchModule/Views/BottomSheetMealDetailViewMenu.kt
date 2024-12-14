package com.TheCooker.Presentation.Views.Modules.SearchModule.Views

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarsModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetMealDetailMenu(content: @Composable (ModalBottomSheetState, () -> Unit) -> Unit,
                              mealsViewModel: MealsViewModel,
                              mealId: String,
                              navController: NavHostController,
                              dialogOpen: MutableState<Boolean>,
                              topBar: TopBarsModel,
                              mealDetail: MealsDetailViewModel
){
    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            BottomSheetMealDetailMenuContent(mealsViewModel = mealsViewModel, recipeId = mealId, scope, navController, dialogOpen, modalSheetState, topBar, mealDetail)

        }
    ) {
        content(modalSheetState) {
            scope.launch {
                modalSheetState.show()
            }
        }

    }

}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomSheetMealDetailMenuContent(mealsViewModel: MealsViewModel, recipeId: String, scope: CoroutineScope,
                                     navController: NavHostController, dialogOpen: MutableState<Boolean>,
                                     modalSheetState: ModalBottomSheetState, topBar: TopBarsModel,
                                     mealDetail: MealsDetailViewModel){
    val context = androidx.compose.ui.platform.LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(color = Color(0xFF202020))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_add_box_24),
                    contentDescription = "Update Recipe",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Update Recipe",
                    fontSize = 20.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable {
                        //todo update recipe
                        scope.launch {
                            mealDetail.fetchDetails(recipeId)
                            modalSheetState.hide()
                        }



                        if(!mealDetail.mealsDetailState.value?.loading!! && isInternetAvailable(context)) {
                            navController.navigate("CreateMeal?recipeId=$recipeId") //Περνάω ώς όρισμα το recipeId κατα το navigate στο createMeal. Αφου το αποθηκεύσω στο  backStackEntry(lambda).arguments? το τραβαω απο τον NavHost(TopNavGraph)(Δες) και το περναω

                            topBar.updateBar = true


                        }else{
                            if(mealDetail.mealsDetailState.value?.error != null || !isInternetAvailable(context)) {
                                scope.launch {
                                        Toast.makeText(
                                            context,
                                            "No Internet Connection",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }

                    }
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_delete_24),
                    contentDescription = "Delete Recipe",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Delete Recipe",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable {
                        //todo delete recipe
                        scope.launch {
                            dialogOpen.value = true
                            modalSheetState.hide()
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f)) // Αυτός ο Spacer θα προσαρμόσει το ύψος

        }
    }
    DeleteRecipeAlertDialog(dialogOpen = dialogOpen, navController = navController, mealsViewModel, recipeId, topBar)
}

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeleteRecipeAlertDialog(
    dialogOpen: MutableState<Boolean>,
    navController: NavHostController,
    mealsViewModel: MealsViewModel,
    recipeId: String,
    topBar: TopBarsModel,


    ) {


    if (dialogOpen.value) {
        val scope = rememberCoroutineScope()

        AlertDialog(
            onDismissRequest = {
                dialogOpen.value = false
            },
            confirmButton = {
                TextButton(onClick = {
                    dialogOpen.value = false
                    mealsViewModel.setBackFromDeleteFlagForFetch(true)


                    scope.launch {
                        mealsViewModel.removeRecipeFromList(recipeId, mealsViewModel.combinedMeals.value!!)
                        mealsViewModel.deleteRecipe(recipeId)
                        topBar.mealTopBarRoute = false
                        topBar.menuTopBarRoute = true
                        navController.navigate("SearchView")


                    }
                }) {

                    androidx.compose.material.Text(text = "Confirm", color = Color(0xFFFFC107))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    dialogOpen.value = false
                }) {
                    androidx.compose.material.Text(text = "Cancel", color = Color(0xFFFFC107))
                }
            },
            title = {
                androidx.compose.material.Text(
                    text = "Delete Recipe",
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                androidx.compose.material.Text(
                    text = "Are you sure you want to delete this recipe?",
                    color = Color.White,
                    style = MaterialTheme.typography.subtitle1,
                    fontFamily = FontFamily.Monospace
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFFFC107))
                .padding(2.dp),
            shape = RoundedCornerShape(5.dp),
            backgroundColor = Color(0xFF202020),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    }
}

