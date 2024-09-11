package com.TheCooker.SearchToolBar.Views

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.TheCooker.R
import com.TheCooker.SearchToolBar.RecipeRepo.UserRecipe
import com.TheCooker.SearchToolBar.ViewModels.CreateMealViewModel
import com.example.cooker.ListView.CustomDivider
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CreateMeal(
    viewmodel: CreateMealViewModel = hiltViewModel(),
    categoryId: String?
               ) {

    val creatorId  = remember { mutableStateOf(viewmodel.creatorId)}

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val createMealViewModel: CreateMealViewModel = viewModel()

    val ingredients by remember { mutableStateOf(createMealViewModel.ingredients) }
    val steps by remember {mutableStateOf(createMealViewModel.steps)}
    val mealName by createMealViewModel.mealName

    var selectedIndex by remember { mutableStateOf(-1) }
    val scope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val roundedCornerRadius = 12.dp
    val modifier = Modifier.fillMaxWidth()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
        }
    }



    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = imageUri ?: "android.resource://com.TheCooker/${R.drawable.addmeal}",
                        contentDescription = "Add Meal Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape = RoundedCornerShape(16.dp))
                            .clickable { launcher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_a_photo_24),
                        contentDescription = "Camera Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        tint = Color(0xFFFFE000)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            CustomDivider()
        }

        item {
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Title",
                        color = Color(0xFFFFC107),
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        value = mealName,
                        onValueChange = { createMealViewModel.onMealNameChange(it) },
                        label = { Text("Meal Title") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xAAFFC107),
                            cursorColor = Color(0xFFFFC107),
                            focusedLabelColor = Color(0xFFFFC107),
                            unfocusedLabelColor = Color(0xFFFFC107),
                            focusedTextColor = Color(0xFFFFC107),
                            unfocusedTextColor = Color(0xAAFFC107),
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
            CustomDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Ingredients",
                    color = Color(0xFFFFC107),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        itemsIndexed(ingredients) { index, ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 44.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = ingredient,
                    onValueChange = { newIngredient ->
                        createMealViewModel.updateIngredient(index, newIngredient)
                    },
                    label = { Text("Ingredient ${index + 1}") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFFFC107),
                        unfocusedBorderColor = Color(0xAAFFC107),
                        cursorColor = Color(0xFFFFC107),
                        focusedLabelColor = Color(0xFFFFC107),
                        unfocusedLabelColor = Color(0xFFFFC107),
                        focusedTextColor = Color(0xFFFFC107),
                        unfocusedTextColor = Color(0xAAFFC107),
                        containerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        selectedIndex = index
                        scope.launch { modalSheetState.show() }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFFFFC107)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Ingredient",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color(0xFFFFC107))

                Text("Add Ingredient",
                    modifier =  Modifier.clickable {createMealViewModel.addIngredientAtEnd()
                },
                    fontSize = 20.sp,
                    color = Color(0xFFFFC107),
                    fontFamily = FontFamily.Monospace
                )

            }
            CustomDivider()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Execution",
                    color = Color(0xFFFFC107),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }
        }
            itemsIndexed(steps) { index, step ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 44.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = step,
                        onValueChange = { step ->
                            createMealViewModel.updateSteps(index, step)
                        },
                        label = { Text("Step ${index + 1}") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFFFFC107),
                            unfocusedBorderColor = Color(0xAAFFC107),
                            cursorColor = Color(0xFFFFC107),
                            focusedLabelColor = Color(0xFFFFC107),
                            unfocusedLabelColor = Color(0xFFFFC107),
                            focusedTextColor = Color(0xFFFFC107),
                            unfocusedTextColor = Color(0xAAFFC107),
                            containerColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            selectedIndex = index
                            scope.launch { modalSheetState.show() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFFFFC107)
                        )
                    }
                }
            }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add Steps",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color(0xFFFFC107))

                Text("Add Step",
                    modifier =  Modifier.clickable {createMealViewModel.addStepAtTheEnd()
                    },
                    fontSize = 20.sp,
                    color = Color(0xFFFFC107),
                    fontFamily = FontFamily.Monospace
                )

            }
            CustomDivider()
        }


        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
                ) {
                Button(
                    onClick = { viewmodel.saveRecipe(
                        UserRecipe(
                            recipeName = createMealViewModel.mealName.value,
                            recipeIngredients = createMealViewModel.ingredients,
                            steps = createMealViewModel.steps,
                            recipeImage = imageUri.toString(),
                            creatorId = creatorId.value ?: "",
                            recipeId =  UUID.randomUUID().toString(),
                            categoryId = categoryId ?: ""
                        )
                    )

                              },
                    modifier = Modifier.width(150.dp),
                    colors = ButtonColors(
                        containerColor = Color(0xFFFFC107),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFFFC107),
                        disabledContentColor = Color.White,
                    ),
                    shape =ShapeDefaults.ExtraSmall
                ) {
                    Text(text = "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                }
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Button(
                    onClick = { /*TODO Implemendtion of save Recipe/ */ },
                    modifier = Modifier
                        .width(150.dp)
                        .padding(start = 8.dp),
                    colors = ButtonColors(
                        containerColor = Color(0xFF28d14f),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFFFC107),
                        disabledContentColor = Color.White
                    ),
                    shape = ShapeDefaults.ExtraSmall
                ) {
                    Text(text = "Share",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)

                }
            }
        }
    }




    // Modal Bottom Sheet Layout
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = roundedCornerRadius, topEnd = roundedCornerRadius),
        sheetContent = {
            IngredientBottomSheetContent(
                modifier = modifier,
                onAddClick = {
                    if (selectedIndex >= 0) {
                        createMealViewModel.addIngredientAfter(selectedIndex)
                    }
                    scope.launch { modalSheetState.hide() }
                },
                onDeleteClick = {
                    if (selectedIndex >= 0) {
                        createMealViewModel.removeIngredient(selectedIndex)
                    }
                    scope.launch { modalSheetState.hide() }
                }
            )
        }
    ) {

    }
    // Modal Bottom Sheet Layout
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = roundedCornerRadius, topEnd = roundedCornerRadius),
        sheetContent = {
            StepBottomSheetContent(
                modifier = modifier,
                onAddClick = {
                    if (selectedIndex >= 0) {
                        createMealViewModel.addStepAfter(selectedIndex)
                    }
                    scope.launch { modalSheetState.hide() }
                },
                onDeleteClick = {
                    if (selectedIndex >= 0) {
                        createMealViewModel.removeStep(selectedIndex)
                    }
                    scope.launch { modalSheetState.hide() }
                }
            )
        }
    ) {

    }

}

@Composable
fun IngredientBottomSheetContent(
    modifier: Modifier,

    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Ορίστε το μέγιστο ύψος για το sheet
            .background(color = Color(0xFF202020))
    ) {
        Column(
            modifier = modifier.padding(16.dp),
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
                    contentDescription = "Add Ingredient",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Add Ingredient",
                    fontSize = 20.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onAddClick() }
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
                    contentDescription = "Remove Ingredient",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Remove Ingredient",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onDeleteClick() }
                )
            }
            Spacer(modifier = Modifier.weight(1f)) // Αυτός ο Spacer θα προσαρμόσει το ύψος

        }
    }
}

@Composable
fun StepBottomSheetContent(
    modifier: Modifier,

    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp) // Ορίστε το μέγιστο ύψος για το sheet
            .background(color = Color(0xFF202020))
    ) {
        Column(
            modifier = modifier.padding(16.dp),
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
                    contentDescription = "Add Step",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Add Step",
                    fontSize = 20.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onAddClick() }
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
                    contentDescription = "Remove Step",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color.White
                )
                Text(
                    text = "Remove Step",
                    fontSize = 21.sp,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.clickable { onDeleteClick() }
                )
            }
            Spacer(modifier = Modifier.weight(1f)) // Αυτός ο Spacer θα προσαρμόσει το ύψος

        }
    }
}





/*
            LazyRow(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                item{
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center) {
                        TextField(value = {}, onValueChange = )
                    }
                }

            }
 */