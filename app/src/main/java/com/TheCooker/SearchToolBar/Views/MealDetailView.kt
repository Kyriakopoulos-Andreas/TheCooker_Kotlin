package com.TheCooker.SearchToolBar.Views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.TheCooker.SearchToolBar.ApiService.MealDetail
import com.TheCooker.SearchToolBar.ViewModels.MealsDetailViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealDetailView(
    detailViewModel: MealsDetailViewModel,
    details: List<MealDetail>
) {
    val detailState = detailViewModel.mealsDetailState.value

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (detailState!!.loading) {
            CircularProgressIndicator()
        } else if (detailState.error != null) {
            Text(text = detailState.error, color = MaterialTheme.colorScheme.error)
        } else {
            details.forEach { detail ->
                ViewDetails(detail)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewDetails(detail: MealDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = detail.strMeal,
            textAlign = TextAlign.Center,
            style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.W500),
            modifier = Modifier.padding(4.dp),
            color = Color.White
            )


        Image(
            painter = rememberAsyncImagePainter(model = detail.strMealThumb),
            contentDescription = "${detail.strMeal} Thumbnail",
            modifier = Modifier
                .wrapContentSize()
                .aspectRatio(1f)
        )

        for ((ingredient, measure) in detail.getIngredientsWithMeasures()) {
            Text(
                text = "${ingredient ?: "Unknown"}: ${measure ?: "No measure"}",
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(8.dp),
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Text(
            text = detail.strInstructions,
            textAlign = TextAlign.Justify,
            modifier = Modifier.padding(8.dp),
            color = Color.White
        )

        if (detail.strYoutube.isNotEmpty()) {
            Text(
                text = detail.strYoutube,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp),
                style = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.primary),
                color = Color(0xFFFFC107)
            )
        }
    }
}
















