package com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealModel
import com.TheCooker.dataLayer.Repositories.RecipeRepo
import com.TheCooker.dataLayer.dto.MealItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

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



    private val _userAddedRecipes = MutableLiveData<List<UserMealDetailModel>>(emptyList())
    private val userAddedRecipes: List<UserMealDetailModel> get() = _userAddedRecipes.value ?: emptyList()
    private val _combinedMeals = MutableLiveData<MutableList<MealItem>>(mutableListOf()) // Δημιουργούμε το LiveData ως MutableList του τυπου MealItem για να χρησιμοποιήσουμε observers.
    val combinedMeals: LiveData<MutableList<MealItem>> get() = _combinedMeals

    private val _backFromUpdate = MutableStateFlow<Boolean>(value = false)
    val backFromUpdate: StateFlow<Boolean> get()  = _backFromUpdate

    private val _backFromDeleteFlagForFetch = MutableStateFlow<Boolean>(value = false)
    val backFromDeleteFlagForFetch: StateFlow<Boolean> get()  = _backFromDeleteFlagForFetch

    private val _updatedMeals = MutableStateFlow<List<UserMealModel>>(emptyList())
    val updatedMeals: StateFlow<List<UserMealModel>> = _updatedMeals

    fun setUpdatedMeals(meals: List<UserMealModel>) {
        _updatedMeals.value = meals
    }

    fun setBackFromUpdate(value: Boolean) {
        _backFromUpdate.value = value
    }

    fun setBackFromDeleteFlagForFetch(value: Boolean) {
        _backFromDeleteFlagForFetch.value = value
    }

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


    fun updateRecipeOnLiveList(recipe: UserMealDetailModel, mealsExist: MutableList<MealItem>) {
        recipe.recipeId?.let {

            removeRecipeFromList(it, mealsExist)
        }
        addRecipe(recipe, mealsExist)
    }

    fun addRecipe(recipe: UserMealDetailModel, mealsExist: MutableList<MealItem>) {

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

    fun removeRecipeFromList(recipeId: Any, mealsExist: MutableList<MealItem>) {
        Log.d("UpdatedList1", mealsExist.toString())

        val updatedList = mealsExist.filter { it.id != recipeId }.toMutableList()
        Log.d("UpdatedList", updatedList.toString())


        mealsExist.clear()
        mealsExist.addAll(updatedList)
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
                UserMealDetailModel(
                    it.categoryId,
                    it.recipeId,
                    it.recipeName,
                    recipeImage = it.recipeImage
                )
            }
            val apiMeals = apiMealsFromApiFirebase.map {
                UserMealModel(it.strMeal, it.strMealThumb, it.idMeal)
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
