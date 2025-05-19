package com.TheCooker.Presentation.Views.Modules.SearchModule.Views

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.TheCooker.Common.Layer.NavGraphs.TopNavGraphSharedViewModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.CategoryModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.MealsViewModel
import com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels.CategoryViewModel


@Composable
fun SearchView(
    recipeState: CategoryViewModel.RecipeState,
    navigateToMeals: (CategoryModel) -> Unit,
    fetchMeals: (String) -> Unit,
    mealsViewModel: MealsViewModel,
    topNavGraphSharedViewModel : TopNavGraphSharedViewModel
               ){


    val context = LocalContext.current
    val create = if(mealsViewModel.mealState.value?.loading == true || mealsViewModel.mealState.value?.loading == true)
        false
    else true



    Box(modifier = Modifier.fillMaxSize(),

        ){

        when{
            recipeState.loading ->{
                CircularProgressIndicator()
            }

            recipeState.error != null ->{
                Toast.makeText(context, "There was an error!", Toast.LENGTH_SHORT).show()

            }
            else ->{
                ShowCategories(categories = recipeState.list, navigateToMeals, fetchMeals,create, mealsViewModel, topNavGraphSharedViewModel)
            }
        }
    }

}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ShowCategories(categories: List<CategoryModel>,
                   navigateToMeals: (CategoryModel) -> Unit,
                   fetchMeals: (String) -> Unit,
                   create: Boolean,
                   mealsViewModel: MealsViewModel,
                   topNavGraphSharedViewModel: TopNavGraphSharedViewModel
){





    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()){


        items(categories){
            Category ->
            println(Category.strCategory)
            ShowItem(categoryModel = Category, navigateToMeals, fetchMeals,create, mealsViewModel, topNavGraphSharedViewModel = topNavGraphSharedViewModel)
        }
    }

}

@Composable
fun ShowItem(categoryModel: CategoryModel,
             navigateToMeals: (CategoryModel) -> Unit,
             fetchMeals: (String) -> Unit,
             create: Boolean,
             mealsViewModel: MealsViewModel,
             topNavGraphSharedViewModel: TopNavGraphSharedViewModel
){



    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .clickable (enabled = create){ //ΑΠΕΝΕΡΓΟΠΟΕΙ ΤΑ ΚΟΥΜΠΙΑ ΕΝΟΣΩ ΤΟ DOWNLOAD ΕΙΝΑΙ ΣΕ ΚΑΤΑΣΤΑΣΗ LOADING!!!!!!
            fetchMeals(categoryModel.strCategory?: "")
            navigateToMeals(categoryModel)
            topNavGraphSharedViewModel.setCategoryId(categoryModel.idCategory ?: "")
        },
        horizontalAlignment = Alignment.CenterHorizontally) {


        Image(painter = rememberAsyncImagePainter(model = categoryModel.strCategoryThumb),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f))

        Text(text = categoryModel.strCategory?: "", modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )

    }
}



