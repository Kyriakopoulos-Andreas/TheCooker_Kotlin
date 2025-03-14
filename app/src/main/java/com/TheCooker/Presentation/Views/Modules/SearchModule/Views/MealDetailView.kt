package com.TheCooker.Presentation.Views.Modules.SearchModule.Views

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.ApiMealDetailModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsDetailViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealDetailView(
    detailViewModel: MealsDetailViewModel,
    updateDetails: UserMealDetailModel?
) {
    val detailState = detailViewModel.mealsDetailState.observeAsState()
    Log.d("JoinOnDetails", "Joinnnn")
    Log.d("TestDetailList", "User detail: $updateDetails")





    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            detailState.value!!.loading -> {
                CircularProgressIndicator()
                Log.d("TestPostDetailLoad", "Loading...")
            }
            detailState.value!!.error != null -> {
                Text(text = detailState.value!!.error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                Log.d("TestPostDetailLoad", "error...")
            }
            else -> {
                detailState.value!!.list.forEach { detail ->
                    Log.d("TestPostDetailLoad", "PreJoin...")
                    Log.d("TestDetailList", "User detail: $detail")

                    when (detail) {
                        is MealsDetailViewModel.recipeDetails.ApiMealDetail -> {
                            detail.apiMealDetailModel.forEach { apiDetail ->
                                ViewApiDetails(apiDetail)
                            }
                        }
                        is MealsDetailViewModel.recipeDetails.UserMealDetail -> {
                            Log.d("TestDetailList", "User detail: $detail")
                            detail.mealDetail.forEach { userDetail ->
                                Log.d("UserRecipeTest", "User detail: $userDetail")
                                ViewUserDetails(userDetail,  updateDetails, detailViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewApiDetails(detail: ApiMealDetailModel) {






    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text =  detail.strMeal,
            modifier = Modifier.padding(bottom = 12.dp, top = 12.dp),
            color = Color(0xFFFFC107),
            style = androidx.compose.material.MaterialTheme.typography.h6,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )


        Image(
            painter = rememberAsyncImagePainter(model = detail.strMealThumb),
            contentDescription = "${detail.strMeal} Thumbnail",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20))
                .border(2.dp, Color.Gray, RoundedCornerShape(20))          )

        Text(text = "Ingredients",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp, top = 12.dp),
            color = Color(0xFFFFC107),
            style = androidx.compose.material.MaterialTheme.typography.h6,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        for ((ingredient, measure) in detail.getIngredientsWithMeasures()) {
            Text(
                text = "${ingredient ?: "Unknown"}: ${measure ?: "No measure"}",
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(8.dp),
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
                color = Color.White,
                fontSize = 16.sp,

            )
        }
        Text(text = "Steps",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp, top = 12.dp),
            color = Color(0xFFFFC107),
            style = androidx.compose.material.MaterialTheme.typography.h6,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

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


@Composable
fun ViewUserDetails(detail1: UserMealDetailModel, updateDetails: UserMealDetailModel?, detailViewModel: MealsDetailViewModel){




    var detail by remember { mutableStateOf<UserMealDetailModel>(detail1) }
    if (updateDetails != null) {
        detail = detailViewModel.copyDetails(detail1, updateDetails)

    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        detail.recipeName?.let {
            Text(
                text = it,
                modifier = Modifier.padding(bottom = 12.dp, top = 12.dp),
                color = Color(0xFFFFC107),
                style = androidx.compose.material.MaterialTheme.typography.h6,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Image(
            painter = rememberAsyncImagePainter(model = detail.recipeImage),
            contentDescription = "Image Thumbnail",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20))
                .border(2.dp, Color.Gray, RoundedCornerShape(20))
        )

        Text(text = "Ingredients",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp, top = 12.dp),
            color = Color(0xFFFFC107),
            style = androidx.compose.material.MaterialTheme.typography.h6,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )


            detail.recipeIngredients?.forEach {
                Text(
                    text = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

        Text(text = "Steps",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp, top = 12.dp),
            color = Color(0xFFFFC107),
            style = androidx.compose.material.MaterialTheme.typography.h6,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )



        detail.steps?.forEach {
            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Normal),
                color = Color.White,
                fontSize = 16.sp
            )

        }



    }


}




























//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun ViewUserMealDetails(userMealDetail: UserRecipe?) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (userMealDetail != null) {
//            userMealDetail.name?.let {
//                Text(
//                    text = it,
//                    textAlign = TextAlign.Center,
//                    style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.W500),
//                    modifier = Modifier.padding(4.dp),
//                    color = Color.White
//                )
//            }
//        }
//
//
//        if (userMealDetail != null) {
//            Image(
//                painter = rememberAsyncImagePainter(model = userMealDetail?.image),
//                contentDescription = "${userMealDetail.name} Thumbnail",
//                modifier = Modifier
//                    .wrapContentSize()
//                    .aspectRatio(1f)
//            )
//        }
//
//        if (userMealDetail != null) {
//            for (ingredient in userMealDetail.recipeIngredients!!) {
//                Text(
//                    text = ingredient,
//                    modifier = Modifier
//                        .fillMaxWidth()
//
//                        .padding(8.dp),
//                    style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold),
//                    color = Color.White
//                )
//            }
//        }
//
//        Text(
//            text = userMealDetail?.steps.toString(),
//            textAlign = TextAlign.Justify,
//            modifier = Modifier.padding(8.dp),
//            color = Color.White
//        )
//
//    }
//}




