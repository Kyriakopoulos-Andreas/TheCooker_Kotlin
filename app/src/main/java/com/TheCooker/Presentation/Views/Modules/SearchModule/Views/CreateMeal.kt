package com.TheCooker.Presentation.Views.Modules.SearchModule.Views

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
//noinspection UsingMaterialAndMaterial3Libraries
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.TheCooker.Common.Layer.Check.isInternetAvailable
import com.TheCooker.Domain.Layer.Models.ScreenModels.TopBarsModel
import com.TheCooker.R
import com.TheCooker.dataLayer.dto.MealItem
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Presentation.Views.Modules.Dividers.BlackFatDivider
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CreateMeal(
    recipeId: String?,
    mealDetailViewModel: MealsDetailViewModel,
    createMealViewModel: CreateMealViewModel,
    categoryId: String?,
    saveNavigateBack: () -> Unit,
    navController: NavController,
    mealsViewModel: MealsViewModel,
    combineMeals: MutableList<MealItem>,
    TopBarsModel: TopBarsModel
) {

    LaunchedEffect(Unit) {
        createMealViewModel.resetStates()
    }


    val context = LocalContext.current
    val creatorId  = remember { mutableStateOf(createMealViewModel.creatorId)}
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val ingredients by remember { mutableStateOf(createMealViewModel.ingredients) }
    val steps by remember {mutableStateOf(createMealViewModel.steps)}
    val mealName by createMealViewModel.mealName
    val stepOfCompletion by createMealViewModel.stateOfCompletion.collectAsState()
    val mealNameError by createMealViewModel.mealNameError.collectAsState()
    val ingredientError by createMealViewModel.ingredientsError.collectAsState()
    val stepError by createMealViewModel.stepsError.collectAsState()
    val updatedMealNameError by createMealViewModel.mealNameError.collectAsState()
    val updatedIngredientError by createMealViewModel.ingredientsError.collectAsState()
    val updatedStepError by createMealViewModel.stepsError.collectAsState()
    val saveButtonDisabler by remember { mutableStateOf(createMealViewModel.saveButtonDisabled) }
    val coroutineScope = rememberCoroutineScope()
    var hasLaunched by rememberSaveable { mutableStateOf(false) }







    var selectedIndex by remember { mutableStateOf(-1) }
    val scope = rememberCoroutineScope()

    val modalSheetStateStep = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val modalSheetStateIngredient = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )


    val detailState = mealDetailViewModel.mealsDetailState.observeAsState()
    Log.d("mealForUpdate", detailState.value.toString())
    Log.d("RecipeId", recipeId.toString())
    val roundedCornerRadius = 12.dp
    val modifier = Modifier.fillMaxWidth()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it

        }
    }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    val imageTitleUpdate by remember{mealDetailViewModel.updatedMealName}
    val ingredientsUpdate by remember { mutableStateOf(mealDetailViewModel.updatedIngredients) }
    val stepsUpdate by remember { mutableStateOf(mealDetailViewModel.updatedSteps) }
    var textFieldValue by remember{ mutableStateOf<String>("") }



    LaunchedEffect(detailState.value, recipeId) {
        if(!hasLaunched) {
            Log.d("mealForUpdate", detailState.value.toString())
            if (detailState?.value?.list?.isNotEmpty() == true) {
                val detail = detailState.value?.list?.get(0)
                if (detail is MealsDetailViewModel.recipeDetails.UserMealDetail) {
                    imageUrl = detail.mealDetail.firstOrNull()?.recipeImage
                    hasLaunched = true
                    Log.d("ImageUrlTest", "UserMealDetail found: $imageUrl")
                    detail.mealDetail.firstOrNull()?.recipeName?.let {
                        mealDetailViewModel.updatedMealName(
                            it
                        )
                    }
                    detail.mealDetail.firstOrNull()?.recipeIngredients?.let {
                        mealDetailViewModel.updatedIngredients(
                            it
                        )
                    }
                    detail.mealDetail.firstOrNull()?.steps?.let {
                        mealDetailViewModel.updatedSteps(
                            it
                        )
                    }

                    Log.d("First", "UserMealDetail found: $imageUrl")
                }
            }
            if (recipeId == null) {
                imageUrl = "android.resource://com.TheCooker/${R.drawable.addmeal}"
            }
        }

    }


    val finalImageUrl = imageUri ?: imageUrl




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
                        model = finalImageUrl,
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

            BlackFatDivider()
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

                textFieldValue = if (recipeId != null) imageTitleUpdate else mealName

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
                        value = textFieldValue,
                        onValueChange = {newValue ->
                            if(recipeId != null)
                                mealDetailViewModel.updatedMealName(newValue)
                            else
                                createMealViewModel.setMealName(newValue)
                        },
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
                if(recipeId != null) {
                    updatedMealNameError?.let {
                        if (it != "✔") {
                            Text(
                                text = it,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
                else{
                    mealNameError?.let {
                        if(it != "✔"){
                            Text(
                                text = it,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }

            }

            BlackFatDivider()

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

        val ingredientsValue = if (recipeId != null) ingredientsUpdate else ingredients

        itemsIndexed(ingredientsValue) { index, ingredient ->
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
                        if (recipeId != null)
                            mealDetailViewModel.updateIngredient(index, newIngredient)
                        else{
                            createMealViewModel.updateIngredient(index, newIngredient)
                        }

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
                        scope.launch { modalSheetStateIngredient.show() }
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
            Column(modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "Add Ingredient",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color(0xFFFFC107)
                    )

                    Text(
                        "Add Ingredient",
                        modifier = Modifier.clickable {

                            if (recipeId != null)
                                mealDetailViewModel.addIngredientAtEnd()
                            else
                                createMealViewModel.addIngredientAtEnd()
                        },
                        fontSize = 20.sp,
                        color = Color(0xFFFFC107),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {

                    if (recipeId != null) {
                        updatedIngredientError.let {
                            if (it != "✔") {
                                if (it != null) {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                    } else {
                        ingredientError.let {
                            if (it != "✔") {
                                if (it != null) {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }



            }
            BlackFatDivider()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Steps",
                    color = Color(0xFFFFC107),
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 8.dp)
                )

            }
        }
        val stepsValue = if(recipeId != null){ stepsUpdate} else{ steps}

        itemsIndexed(stepsValue) { index, step ->
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
                        if (recipeId != null){
                            mealDetailViewModel.updateStep(index, step)}
                        else{
                            createMealViewModel.updateSteps(index, step)}
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
                        scope.launch { modalSheetStateStep.show() }
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
            Column(modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_24),
                        contentDescription = "Add Steps",
                        modifier = Modifier.padding(end = 8.dp),
                        tint = Color(0xFFFFC107)
                    )

                    Text(
                        "Add Step",
                        modifier = Modifier.clickable {
                            if (recipeId != null)
                                mealDetailViewModel.addStepAtTheEnd()
                            else
                                createMealViewModel.addStepAtTheEnd()
                        },
                        fontSize = 20.sp,
                        color = Color(0xFFFFC107),
                        fontFamily = FontFamily.Monospace
                    )

                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    if (recipeId != null) {
                        updatedStepError.let {
                            if(it != "✔"){
                                if (it != null) {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                    }else{
                        stepError.let {
                            if(it != "✔"){
                                if (it != null) {
                                    Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }

                    }
                }
            }

            BlackFatDivider()

        }


        item {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp), horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if(!isInternetAvailable(context)){
                            createMealViewModel.setSaveButtonEnabled()
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        createMealViewModel.setSaveButtonDisabled()
                        createMealViewModel.validateSave()
                        coroutineScope.launch {
                            if (recipeId != null) {
                                createMealViewModel.saveOrUpdateRecipe(
                                    visibility = false,
                                    recipeId = recipeId,
                                    mealDetailViewModel = mealDetailViewModel,
                                    createMealViewModel = createMealViewModel,
                                    mealsViewModel = mealsViewModel,
                                    navController = navController,
                                    saveNavigateBack = {
                                        saveNavigateBack()
                                    },
                                    combineMeals = combineMeals,
                                    creatorId = creatorId.toString(),
                                    imageUri = imageUri,
                                    imageUrl = imageUrl,
                                    ingredients = ingredientsUpdate,
                                    mealName = imageTitleUpdate,
                                    steps = stepsUpdate,
                                    categoryId = categoryId,
                                    topBarsModel = TopBarsModel,
                                )
                            } else {

                                if (stepOfCompletion == true) {

                                    createMealViewModel.saveOrUpdateRecipe(
                                        visibility = false,
                                        recipeId = recipeId,
                                        mealDetailViewModel = mealDetailViewModel,
                                        createMealViewModel = createMealViewModel,
                                        mealsViewModel = mealsViewModel,
                                        navController = navController,
                                        saveNavigateBack = {
                                            saveNavigateBack()
                                        },
                                        combineMeals = combineMeals,
                                        creatorId = creatorId.value,
                                        imageUri = imageUri,
                                        imageUrl = imageUrl,
                                        ingredients = ingredients,
                                        mealName = mealName,
                                        steps = steps,
                                        categoryId = categoryId,
                                        topBarsModel = TopBarsModel,
                                    )

                                }else{
                                    createMealViewModel.setSaveButtonEnabled()
                                }
                            }
                        }
                    },
                    modifier = Modifier.width(150.dp),
                    enabled = saveButtonDisabler.value,
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
                if(recipeId == null) {
                    Button(
                        onClick = {

                            if(!isInternetAvailable(context)){
                                createMealViewModel.setSaveButtonEnabled()
                                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            createMealViewModel.setSaveButtonDisabled()
                            createMealViewModel.validateSave()
                            coroutineScope.launch {
                                if (stepOfCompletion == true) {
                                    createMealViewModel.saveOrUpdateRecipe(
                                        visibility = true,
                                        recipeId = recipeId,
                                        mealDetailViewModel = mealDetailViewModel,
                                        createMealViewModel = createMealViewModel,
                                        mealsViewModel = mealsViewModel,
                                        navController = navController,
                                        saveNavigateBack = {
                                            saveNavigateBack()
                                        },
                                        combineMeals = combineMeals,
                                        creatorId = creatorId.value,
                                        imageUri = imageUri,
                                        imageUrl = imageUrl,
                                        ingredients = ingredients,
                                        mealName = mealName,
                                        steps = steps,
                                        categoryId = categoryId,
                                        topBarsModel = TopBarsModel,
                                    )

                                }else{
                                    createMealViewModel.setSaveButtonEnabled()
                                }


                            }


                        },
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
                        Text(
                            text = "Share",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                    }
                }

            }
        }
    }




    // Modal Bottom Sheet Layout
    ModalBottomSheetLayout(
        sheetState = modalSheetStateIngredient,
        sheetShape = RoundedCornerShape(topStart = roundedCornerRadius, topEnd = roundedCornerRadius),
        sheetContent = {
            IngredientBottomSheetContent(
                modifier = modifier,
                onAddClick = {
                    if (selectedIndex >= 0) {
                        if (recipeId != null)
                            mealDetailViewModel.addIngredientAfter(selectedIndex)
                        else
                            createMealViewModel.addIngredientAfter(selectedIndex)
                    }
                    scope.launch { modalSheetStateIngredient.hide() }
                },
                onDeleteClick = {
                    if (selectedIndex >= 0) {
                        if (recipeId != null)
                            mealDetailViewModel.removeIngredient(selectedIndex)
                        else
                            createMealViewModel.removeIngredient(selectedIndex)
                    }
                    scope.launch { modalSheetStateIngredient.hide() }
                }
            )
        }
    ) {

    }
    // Modal Bottom Sheet Layout
    ModalBottomSheetLayout(
        sheetState = modalSheetStateStep,
        sheetShape = RoundedCornerShape(topStart = roundedCornerRadius, topEnd = roundedCornerRadius),
        sheetContent = {
            StepBottomSheetContent(
                modifier = modifier,
                onAddClick = {
                    if (selectedIndex >= 0) {
                        if (recipeId != null)
                            mealDetailViewModel.addStepAfter(selectedIndex)
                        else
                            createMealViewModel.addStepAfter(selectedIndex)
                    }
                    scope.launch { modalSheetStateStep.hide() }
                },
                onDeleteClick = {
                    if (selectedIndex >= 0) {
                        if (recipeId != null){
                            mealDetailViewModel.removeStep(selectedIndex)}
                        else{
                            createMealViewModel.removeStep(selectedIndex)}
                    }
                    scope.launch { modalSheetStateStep.hide() }
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
            .height(130.dp)
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
            Spacer(modifier = Modifier.weight(1f))

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



