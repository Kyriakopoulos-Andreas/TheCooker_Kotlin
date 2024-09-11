package com.TheCooker.SearchToolBar.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.SearchToolBar.ApiService.ApiService
import com.TheCooker.SearchToolBar.RecipeRepo.Category
import com.TheCooker.SearchToolBar.ApiService.MealDetail
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategory
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import com.TheCooker.SearchToolBar.RecipeRepo.UserRecipe

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCategoryViewModel @Inject constructor(
    private val apiService: ApiService,
    private val recipeRepo: RecipeRepo
): ViewModel() {

    data class RecipeState(
        val error: String? = null,
        val loading: Boolean = false,
        val list: List<Category> = emptyList()
    )




    private val _categoriesState = MutableStateFlow(RecipeState())
    val categoriesState: StateFlow<RecipeState> = _categoriesState

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                _categoriesState.value = RecipeState(loading = true)

                // Λήψη τοπικών κατηγοριών από τη βάση δεδομένων
                val localCategories = recipeRepo.getCategories()
                println("Local categories: $localCategories")
                _categoriesState.value = RecipeState(
                    loading = false,
                    list = localCategories
                )

                // Συγχρονισμός κατηγοριών με το API
                val response = apiService.getCategories()
                val categoriesFromApi = response.categories
                recipeRepo.syncApiCategoriesWithFirebase(categoriesFromApi)

                // Λήψη ενημερωμένων κατηγοριών από τη βάση δεδομένων
                val updatedCategories = recipeRepo.getCategories()
                println("Updated categories: $updatedCategories")
                _categoriesState.value = RecipeState(
                    loading = false,
                    error = null,
                    list = updatedCategories
                )
            } catch (e: Exception) {
                println("Error: ${e.message}")
                _categoriesState.value = RecipeState(
                    loading = false,
                    error = "Error occurred"
                )
            }
        }
    }


}

@HiltViewModel
class MealsViewModel@Inject constructor(
    private val apiService: ApiService,
    private val recipeRepo: RecipeRepo
): ViewModel() {

    data class MealsState(
        val loading: Boolean = false,
        val list: List<MealItem> = emptyList(),
        val error: String? = null
    )

    private val _mealState = MutableLiveData(MealsState())
    val mealState: LiveData<MealsState> get() = _mealState

    fun fetchMeals(mealCategory: String, categoryId: String?) {
        _mealState.value = MealsState(loading = true)



        viewModelScope.launch() {
            try {
                val meals = recipeRepo.getRecipes(categoryId ?: "")
                val responseMeals = apiService.getMeals(mealCategory)

                val  mealItems: List<MealItem> = responseMeals.meals.map {
                    MealsCategory(
                        it.strMeal ?: "",
                        it.strMealThumb ?: "",
                        it.idMeal ?: ""
                    )
                } + meals.map {
                    UserRecipe(
                        it.categoryId,
                        it.recipeId,
                        it.recipeName

                    )
                }


                _mealState.postValue(
                    MealsState(
                        loading = false,
                        error = null,
                        list = mealItems
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


@HiltViewModel
class MealsDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    data class MealsDetailState(
        val loading: Boolean = false,
        val error: String? = null,
        val list: List<MealDetail> = emptyList()
    )

    private val _mealsDetailState = MutableLiveData(MealsDetailState())
    val mealsDetailState: LiveData<MealsDetailState> = _mealsDetailState

    fun fetchDetails(meal: String) {
        _mealsDetailState.value = MealsDetailState(loading = true)
        viewModelScope.launch() {
            try {
                val response = apiService.getMealDetail(meal)
                _mealsDetailState.value = MealsDetailState(
                    loading = false,
                    error = null,
                    list = response.meals
                )
            } catch (e: Exception) {
                _mealsDetailState.value = MealsDetailState(
                    loading = false,
                    error = "Error occurred ${e.message}"
                )
            }
        }
    }
}
