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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.TheCooker.SearchToolBar.RecipeRepo.Category
import com.TheCooker.SearchToolBar.ViewModels.MealsViewModel
import com.TheCooker.SearchToolBar.ViewModels.SearchCategoryViewModel


@Composable
fun SearchView(
    recipeState: SearchCategoryViewModel.RecipeState,
    navigateToMeals: (Category) -> Unit,
    fetchMeals: (String) -> Unit,
    mealsViewModel: MealsViewModel
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

                ShowCategories(categories = recipeState.list, navigateToMeals, fetchMeals,create)
            }
        }
    }

}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ShowCategories(categories: List<Category>,
                   navigateToMeals: (Category) -> Unit,

                   fetchMeals: (String) -> Unit,
                   create: Boolean){





    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()){


        items(categories){

            Category ->
            println(Category.strCategory)
            ShowItem(category = Category, navigateToMeals, fetchMeals,create)
        }
    }

}

@Composable
fun ShowItem(category: Category,
             navigateToMeals: (Category) -> Unit,
             fetchMeals: (String) -> Unit,
             create: Boolean){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp)
        .clickable (enabled = create){ //ΑΠΕΝΕΡΓΟΠΟΕΙ ΤΑ ΚΟΥΜΠΙΑ ΕΝΟΣΩ ΤΟ DOWNLOAD ΕΙΝΑΙ ΣΕ ΚΑΤΑΣΤΑΣΗ LOADING!!!!!!
            fetchMeals(category.strCategory?: "")
            navigateToMeals(category)

        },
        horizontalAlignment = Alignment.CenterHorizontally) {


        Image(painter = rememberAsyncImagePainter(model = category.strCategoryThumb),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f))

        Text(text = category.strCategory?: "", modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(fontWeight = FontWeight.SemiBold),
            color = Color.White
        )

    }
}



