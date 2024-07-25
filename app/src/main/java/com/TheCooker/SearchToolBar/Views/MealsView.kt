package com.TheCooker.SearchToolBar.Views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.TheCooker.SearchToolBar.ApiService.MealsCategory
import com.TheCooker.SearchToolBar.ViewModels.MealsViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MealsView(mealsState: MealsViewModel.MealsState,
              meals: List<MealsCategory>,
              navigateToDetails: (MealsCategory) -> Unit,
              fetchDetails: (String) -> Unit) {



    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            mealsState.loading -> {
                CircularProgressIndicator()
            }
            mealsState.error != null -> {
                Toast.makeText(context, "There was an error!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                ViewMealsList(meals = meals, navigateToDetails, fetchDetails, )
            }
        }
    }
}

@Composable
fun ViewMealsList(meals: List<MealsCategory>,
                  navigateToDetails: (MealsCategory)->Unit,
                  fetchDetails: (String) -> Unit,
              ){
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),

    ) {
        items(meals){
            meals ->
            ViewMeal(mealsCategory = meals, navigateToDetails,fetchDetails)

        }
    }
}

@Composable
fun ViewMeal(mealsCategory: MealsCategory,
             navigateToDetails: (MealsCategory)->Unit,
             fetchDetails: (String) -> Unit){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .clickable {
            navigateToDetails(mealsCategory)
            fetchDetails(mealsCategory.strMeal)
        },   //  ,<-------------------------------------------
        horizontalAlignment = Alignment.Start) {

        Image(painter = rememberAsyncImagePainter(model = mealsCategory.strMealThumb),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f))


        Text(text = mealsCategory.strMeal,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold),
            color = Color.White
        )

    }
}