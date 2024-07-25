package com.TheCooker.SearchToolBar.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.SearchToolBar.ApiService.Category
import com.TheCooker.SearchToolBar.ApiService.MealDetail
import com.TheCooker.SearchToolBar.ApiService.MealsCategory
import com.TheCooker.SearchToolBar.ApiService.recipeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchCategoryViewModel: ViewModel() {

    data class RecipeState(
        val error: String? = null,
        val loading: Boolean = false,
        val list: List<Category> = emptyList()
    ) {

    }

    private val _categoriesState = mutableStateOf(RecipeState())
    val categoriesState: State<RecipeState> = _categoriesState

    init {
        fetchCategories()
    }

    private fun fetchCategories(){
        viewModelScope.launch(Dispatchers.IO){
            try{
                val response = recipeService.getCategories()
                _categoriesState.value = _categoriesState.value.copy(
                    loading = false,
                    error = null,
                    list = response.categories
                )

            }catch(e: Exception){
                _categoriesState.value = _categoriesState.value.copy(
                    loading = false,
                    error = "Error occurred"
                )

            }

        }
    }
}

class MealsViewModel : ViewModel() {

    data class MealsState(
        val loading: Boolean = false,
        val list: List<MealsCategory> = emptyList(),
        val error: String? = null
    )

    private val _mealState = MutableLiveData(MealsState())
    val mealState: LiveData<MealsState> get() = _mealState

    fun fetchMeals(mealCategory: String) {
        _mealState.value = MealsState(loading = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseMeals = recipeService.getMeals(mealCategory)
                _mealState.postValue(
                    MealsState(
                        loading = false,
                        error = null,
                        list = responseMeals.meals
                    )
                )
            } catch (e: Exception) {
                _mealState.postValue(
                    MealsState(
                        loading = false,
                        error = "Error occurred: ${e.message}"
                    )
                )
            }
        }
    }
}



class MealsDetailViewModel: ViewModel() {
    data class MealsDetailState(
        val loading: Boolean = false,
        val error: String? = null,
        val list: List<MealDetail> = emptyList()
    )
   private val _mealsDetailState = MutableLiveData(MealsDetailState())
    val mealsDetailState: LiveData<MealsDetailState> = _mealsDetailState


    fun fetchDetails(meal: String){
        _mealsDetailState.value = MealsDetailState(loading = true)
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response= recipeService.getMealDetail(meal)
                _mealsDetailState.postValue(
                    MealsDetailState(
                        loading = false,
                        error = null,
                        list = response.meals

                    )
                )


            }catch (e: Exception){
                _mealsDetailState.postValue(
                    MealsDetailState(
                        loading = false,
                        error = "Error occured ${e.message}"

                    )

                )



            }

        }

    }

}