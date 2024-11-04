package com.TheCooker.SearchToolBar.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.SearchToolBar.RecipeRepo.Category
import com.TheCooker.SearchToolBar.RecipeRepo.MealDetail
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategory
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import com.TheCooker.SearchToolBar.ApiService.UserRecipe

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchCategoryViewModel @Inject constructor(
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
class MealsViewModel @Inject constructor(

    private val recipeRepo: RecipeRepo
) : ViewModel() {

    data class ApiMealsState(
        val loading: Boolean = false,
        val list: List<MealItem> = emptyList(),
        val error: String? = null
    )

    data class UserMealsState(
        val loading: Boolean = false,
        val list: List<MealItem> = emptyList(),
        val error: String? = null
    )

    private val _mealState = MutableLiveData(ApiMealsState())
    val mealState: LiveData<ApiMealsState> get() = _mealState

    private val _userMealState = MutableLiveData(UserMealsState())
    val userMealState: LiveData<UserMealsState> get() = _userMealState



    private val _userAddedRecipes = MutableLiveData<List<UserRecipe>>(emptyList())
    private val userAddedRecipes: List<UserRecipe> get() = _userAddedRecipes.value ?: emptyList()
    private val _combinedMeals = MutableLiveData<MutableList<MealItem>>(mutableListOf()) // Δημιουργούμε το LiveData ως MutableList του τυπου MealItem για να χρησιμοποιήσουμε observers.
    val combinedMeals: LiveData<MutableList<MealItem>> get() = _combinedMeals

    private val _loading = MediatorLiveData<Boolean>().apply {
        addSource(_mealState) { value = it.loading }
        addSource(_userMealState) { value = it.loading }
    }
    val loading: LiveData<Boolean> get() = _loading

    suspend fun deleteRecipe(recipe: String){
        try {
            recipeRepo.deleteRecipe(recipe)
        }catch (e: Exception){
            Log.d("RecipeViewModel", "Error deleting recipe: ${e.message}")
        }
        //TODO Ισως χρειαστεί μετα το delete να ξανα κανεις fetch τις συνταγες

    }




    fun addRecipe(recipe: UserRecipe, mealsExist: MutableList<MealItem>) {
        mealsExist.add(0, recipe)
        Log.d("mealsExist", mealsExist.toString())
        _mealState.postValue(
            ApiMealsState(
                loading = false,
                list = mealsExist,
                error = null
            )
        )
        _userMealState.postValue(
            UserMealsState(
                loading = false,
                list = _combinedMeals.value ?: mutableListOf(),
                error = null
            )
        )
    }
     fun removeRecipeFromList(recipeId: String, mealsExist: MutableList<MealItem>) {
        Log.d("UpdatedList1", mealsExist.toString())
        val updatedList = mealsExist.filter { it.id!= recipeId }.toMutableList()
        _userMealState.postValue(
            UserMealsState(
                loading = false,
                list = updatedList,
                error = null
            )
        )
        Log.d("UpdatedList", updatedList.toString())


    }

    suspend fun fetchMeals(mealCategory: String, categoryId: String){
        try {
            Log.d("PreFetch", "categoryId: $categoryId")
            if (_loading.value == true) return
            Log.d("categoryId", categoryId.toString())

            _loading.value = true
            _userMealState.value = UserMealsState(loading = true)
            _mealState.value = ApiMealsState(loading = true)

            val meals = recipeRepo.getRecipes(categoryId ?: "")

            val apiMealsFromApiFirebase = recipeRepo.getApiRecipesFromFirestore(categoryId ?: "")


            val userRecipes = meals.map {
                UserRecipe(
                    it.categoryId,
                    it.recipeId,
                    it.recipeName,
                    recipeImage = it.recipeImage
                )
            }
           val apiMeals = apiMealsFromApiFirebase.map {
               MealsCategory(it.strMeal, it.strMealThumb, it.idMeal)
           }



            _combinedMeals.value = (apiMeals + userRecipes + userAddedRecipes).toMutableList()
            Log.d("CombinedMeals2", _combinedMeals.value.toString())

            _mealState.postValue(
                ApiMealsState(
                    loading = false,
                    error = null,
                    list = _combinedMeals.value ?: mutableListOf()
                )
            )

            _userMealState.postValue(
                UserMealsState(
                    loading = false,
                    error = null,
                    list = _combinedMeals.value ?: mutableListOf()
                )
            )
        } catch (e: Exception) {
            _mealState.postValue(
                ApiMealsState(
                    loading = false,
                    error = "Error occurred: ${e.message}"
                )
            )
            _userMealState.postValue(
                UserMealsState(
                    loading = false,
                    error = "Error occurred: ${e.message}"
                )
            )
        }
    }

    fun resetState() {
        _mealState.value = ApiMealsState(loading = false, list = emptyList(), error = null)
        _userMealState.value = UserMealsState(loading = false, list = emptyList(), error = null)
        _userAddedRecipes.value = emptyList()
        _combinedMeals.value = mutableListOf()
    }
}









@Suppress("IMPLICIT_CAST_TO_ANY")
@HiltViewModel
class MealsDetailViewModel @Inject constructor(
    private val recipeRepo: RecipeRepo
) : ViewModel() {

    sealed class recipeDetails{
        data class ApiMealDetail(val mealDetail: List<MealDetail>): recipeDetails()
        data class UserMealDetail(val mealDetail: List<UserRecipe>): recipeDetails()
    }

    data class MealsDetailState(
        val loading: Boolean = false,
        val error: String? = null,
        val list: List<recipeDetails
                > = emptyList()
    )

   suspend fun updateRecipe(recipe: UserRecipe){
       try {
           recipeRepo.updateRecipe(recipe)
       }catch (e: Exception){
           Log.d("RecipeViewModel", "Error updating recipe: ${e.message}")
       }


    }

    private val _mealsDetailState = MutableLiveData(MealsDetailState())
    val mealsDetailState: LiveData<MealsDetailState> = _mealsDetailState

    fun fetchDetails(mealId: String) {
        _mealsDetailState.value = MealsDetailState(loading = true)
        Log.d("MealsDetailViewModel", "Fetching details for meal: $mealId")
        viewModelScope.launch() {
            try {
                val response: recipeDetails = if (mealId.all { it.isDigit() }) {
                    // Fetch details from API collection.
                    val apiDetail = recipeRepo.getApiDetailsFromFirestore(mealId)
                    recipeDetails.ApiMealDetail(apiDetail)

                } else {
                    // Fetch details from user collection
                    val userDetail = recipeRepo.getUserRecipeDetails(mealId)

                   recipeDetails.UserMealDetail(userDetail)
                }
                _mealsDetailState.value = MealsDetailState(
                    loading = false,
                    error = null,
                    list = listOf(response)
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



        //    @HiltViewModel
//    class MealsDetailViewModel @Inject constructor(
//        private val apiService: ApiService,
//        private val recipeRepo: RecipeRepo
//    ) : ViewModel() {
//
//        data class MealsDetailState(
//            val loading: Boolean = false,
//            val error: String? = null,
//            val list: List<MealDetail> = emptyList()
//        )
//
//        data class UserMealDetailState(
//            val loading: Boolean = false,
//            val error: String? = null,
//            val userRecipe: UserRecipe? = null
//        )
//
//        private val _mealsDetailState = MutableLiveData(MealsDetailState())
//        val mealsDetailState: LiveData<MealsDetailState> = _mealsDetailState
//
//        private val _userMealDetailState = MutableLiveData(UserMealDetailState())
//        val userMealDetailState: LiveData<UserMealDetailState> = _userMealDetailState
//
//        fun fetchDetails(meal: String, isUserRecipe: Boolean) {
//
//            if (isUserRecipe) {
//                _mealsDetailState.value = MealsDetailState(loading = true)
//                viewModelScope.launch() {
//                    try{
//                    val response = recipeRepo.getDetails(meal)
//                    _userMealDetailState.value = UserMealDetailState(
//                        loading = false,
//                        error = null,
//                        userRecipe = response.userMeal)
//                    }catch (e: Exception) {
//                        _userMealDetailState.value = UserMealDetailState(
//                            loading = false,
//                            error = "Error occurred ${e.message}"
//                        )
//                    }
//                }
//            }
//            else {
//                _mealsDetailState.value = MealsDetailState(loading = true)
//        viewModelScope.launch() {
//            try {
//                val response = apiService.getMealDetail(meal)
//                _mealsDetailState.value = MealsDetailState(
//                    loading = false,
//                    error = null,
//                    list = response.meals
//                )
//            } catch (e: Exception) {
//                _mealsDetailState.value = MealsDetailState(
//                    loading = false,
//                    error = "Error occurred ${e.message}"
//                )
//            }
//        }
//
//            }
//        }
//    }
