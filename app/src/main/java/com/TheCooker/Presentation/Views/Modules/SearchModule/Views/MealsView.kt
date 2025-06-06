package com.TheCooker.Presentation.Views.Modules.SearchModule.Views

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.TheCooker.R
import com.TheCooker.dataLayer.dto.MealItem
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CreateMealViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MealsView(
    apiMealsState: MealsViewModel.ApiMealsState,
    meals: List<UserMealModel>,
    navigateToDetails: (MealItem) -> Unit,
    navController: NavController,
    createMeal: () -> Unit,
    mealsViewModel: MealsViewModel,
    createMealViewModel: CreateMealViewModel,
) {
    Log.d("MealsViewTest22", "Meals: $meals")
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            apiMealsState.loading -> {
                CircularProgressIndicator()
            }
            apiMealsState.error != null -> {
                Toast.makeText(context, "There was an error!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                ViewMealsList(
                    meals = meals,
                    navigateToDetails = navigateToDetails,
                    createMeal = createMeal,
                    createMealViewModel = createMealViewModel
                )
            }
        }
    }
}

@Composable
fun ViewMealsList(
    meals: List<MealItem>,
    navigateToDetails: (MealItem) -> Unit,
    createMeal: () -> Unit,
    createMealViewModel: CreateMealViewModel,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()  // Γεμίζει το πλάτος της οθόνης
                    .padding(8.dp)
            ) {
                Text(
                    text = "Meals",
                    modifier = Modifier.align(Alignment.Center),  // Κεντράρισμα του κειμένου στο Box
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        item {
            val imageUrl = "android.resource://com.TheCooker/" + R.drawable.add_meal2
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = null,
                // Αντί για fillMaxSize, χρησιμοποιούμε fillMaxWidth για να μην καταλαμβάνει υπερβολικό χώρο,
                // προσθέτουμε clip για στρογγυλεμένες γωνίες και λίγο padding.
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        createMeal()
                        createMealViewModel.onCreateTrue()
                    },
                contentScale = ContentScale.Crop
            )
        }

        items(meals) { mealItem ->
            ViewMeal(
                mealItem = mealItem,
                navigateToDetails = navigateToDetails,
            )
        }
    }
}

@Composable
fun ViewMeal(
    mealItem: MealItem,
    navigateToDetails: (MealItem) -> Unit,
) {
    Log.d("MealItemBack", mealItem.name.toString())
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                scope.launch {
                    navigateToDetails(mealItem)
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imagePainter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(mealItem.image)
                .size(Size.ORIGINAL)
                .build()
        )
        Log.d("MealItem", "Image URL: ${mealItem.image}")
        Log.d("MealItem", "Image Name: ${mealItem.name}")

        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = mealItem.name ?: "",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Center),  // Το κείμενο είναι κεντραρισμένο στο Box
                style = TextStyle(fontWeight = FontWeight.Bold),
                color = Color.White,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


