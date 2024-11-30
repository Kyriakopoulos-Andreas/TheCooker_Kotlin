package com.TheCooker.SearchToolBar.ViewModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.R
import com.TheCooker.SearchToolBar.RecipeRepo.Category
import com.TheCooker.SearchToolBar.RecipeRepo.MealDetail
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem
import com.TheCooker.SearchToolBar.RecipeRepo.MealsCategory
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import com.TheCooker.SearchToolBar.ApiService.UserRecipe

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _backFromUpdate = MutableStateFlow<Boolean>(value = false)
    val backFromUpdate: StateFlow<Boolean> get()  = _backFromUpdate

    private val _backFromDeleteFlagForFetch = MutableStateFlow<Boolean>(value = false)
    val backFromDeleteFlagForFetch: StateFlow<Boolean> get()  = _backFromDeleteFlagForFetch

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


    fun updateRecipeOnLiveList(recipe: UserRecipe, mealsExist: MutableList<MealItem>){
            recipe.recipeId?.let { removeRecipeFromList(it, mealsExist) }
            addRecipe(recipe, mealsExist)

    }

    fun addRecipe(recipe: UserRecipe, mealsExist: MutableList<MealItem>) {
        mealsExist.add(0, recipe)
        Log.d("mealsExist", mealsExist.toString())
        _mealState.postValue(
            ApiMealsState(
                loading = false,
                list = mealsExist ,
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
         _mealState.postValue(
             ApiMealsState(
                 loading = false,
                 list = updatedList,
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

    data class updateRecipe(
        val error: String? = null,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val uploadingImage: Boolean = false,
        var onUpdateRecipe: Boolean = false,
        val imageUploaded: Boolean = false
    )

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
    private val _mealsDetailState = MutableLiveData(MealsDetailState())
    val mealsDetailState: LiveData<MealsDetailState> = _mealsDetailState

    fun copyDetails(details: UserRecipe, updateDetails: UserRecipe?): UserRecipe {
        return copyObj(details, updateDetails)
    }

    private fun copyObj(details: UserRecipe, updateDetails: UserRecipe?): UserRecipe {
        val updatedDetail = details.copy(
            recipeName = updateDetails?.recipeName ?: details.recipeName,
            recipeIngredients = updateDetails?.recipeIngredients ?: details.recipeIngredients,
            steps = updateDetails?.steps ?: details.steps,
            recipeImage = updateDetails?.recipeImage ?: details.recipeImage
        )
        return updatedDetail
    }









    private val _updatedMealName = mutableStateOf("")
    val updatedMealName: State<String> = _updatedMealName

    private val _updatedIngredients = mutableStateListOf<String>("", "")
    val updatedIngredients: List<String> = _updatedIngredients

    private val _updatedSteps = mutableStateListOf<String>("", "")
    val updatedSteps: List<String> = _updatedSteps

    private val _updatedMealNameError = MutableStateFlow<String?>(null)
    val updatedMealNameError: MutableStateFlow<String?> get() = _updatedMealNameError

    private val _updatedIngredientsError = MutableStateFlow<String?>(null)
    val ingredientsError: MutableStateFlow<String?> get() = _updatedIngredientsError

    private val _updatedStepsError = MutableStateFlow<String?>(null)
    val stepsError: MutableStateFlow<String?> get() = _updatedStepsError

    private val _updatedStateOfCompletion = MutableStateFlow<Boolean?>(false)
    val stateOfCompletion: MutableStateFlow<Boolean?> get() = _updatedStateOfCompletion
    private val _updateState = MutableLiveData(updateRecipe())
    val updateState: LiveData<updateRecipe> = _updateState

    fun onUpdateTrue() {
        _updateState.value = _updateState.value?.copy(onUpdateRecipe = true)
    }

    fun onUpdateFalse() {
        _updateState.value = _updateState.value?.copy(onUpdateRecipe = false)
    }





    fun updatedMealName(name: String){
        _updatedMealName.value = name
    }
    fun updatedIngredients(ingredients: List<String>){
        _updatedIngredients.clear()
        _updatedIngredients.addAll(ingredients)
    }
    fun updateIngredient(index: Int, newValue: String) {
        _updatedIngredients[index] = newValue
    }

    fun addIngredientAtEnd() {
        _updatedIngredients.add("")
    }

    fun addIngredientAfter(index: Int) {
        _updatedIngredients.add(index + 1, "")

    }

    fun removeIngredient(index: Int) {
        if (_updatedIngredients.size > 2) {
            _updatedIngredients.removeAt(index)
        }
    }

    private fun validateTitle() {
        if (_updatedMealName.value.isEmpty()) {
            _updatedMealNameError.value = "Meal name cannot be empty"

        } else {
            updatedMealNameError.value = "✔"
        }

    }

    private fun validateIngredients() {
        var allNull = true

        for(ingredient in _updatedIngredients){
            if(ingredient != "" ){
                allNull = false
                break

            }

        }
        if (allNull) {
            _updatedIngredientsError.value = "Meal must have at least one ingredient"
        } else {
            _updatedIngredientsError.value = "✔"
        }
    }
    private fun validateSteps() {

        var allNull = true
        for (step in _updatedSteps) {
            if (step != "") {
                allNull = false
                break
            }

        }
        if (allNull) {
            _updatedStepsError.value = "Meal must have at least one step"
        } else {
            _updatedStepsError.value = "✔"

        }
    }

    fun validateUpdate() {
        validateTitle()
        validateIngredients()
        validateSteps()
        _updatedStateOfCompletion.value = _updatedMealNameError.value == "✔" && _updatedIngredientsError.value == "✔" && _updatedStepsError.value == "✔"


    }



    fun updatedSteps(steps: List<String>){
        _updatedSteps.clear()
        _updatedSteps.addAll(steps)
    }

    fun updateStep(index: Int, newValue: String) {
        _updatedSteps[index] = newValue
    }

    fun addStepAtTheEnd() {
        _updatedSteps.add("")
    }

    fun addStepAfter(index: Int) {
        _updatedSteps.add(index + 1, "")
    }

    fun removeStep(index: Int) {
        if (_updatedSteps.size > 2) {
            _updatedSteps.removeAt(index)
        }
    }





    suspend fun updateRecipe(recipe: UserRecipe, imageUri: Uri?, onSuccess: () -> Unit): UserRecipe{
        _updateState.value = _updateState.value?.copy(isSaving = true)

        val cleanedIngredients = recipe.recipeIngredients?.filter { it.isNotEmpty() }
        val cleanedSteps = recipe.steps?.filter { it.isNotEmpty() }

        val cleanedRecipe = recipe.copy(
            recipeIngredients = cleanedIngredients,
            steps = cleanedSteps,
            creatorId = recipe.creatorId,
            categoryId = recipe.categoryId,


        )


        viewModelScope.launch {
            try {
                if (imageUri != null) {
                    _updateState.value = _updateState.value?.copy(uploadingImage = true)


                    Log.d("SaveStateValueUploadingImage", "${_updateState.value?.uploadingImage}")

                    val downloadUrl = withContext(Dispatchers.IO) {
                        recipeRepo.uploadImageAndGetUrl(imageUri)
                    }

                    Log.d("TestDownLoadUrl1", "Image URL: $downloadUrl")
                    cleanedRecipe.recipeImage = downloadUrl
                    Log.d("TestDownLoadUrl", "Image URL: ${cleanedRecipe.recipeImage}")


                } else {
                    cleanedRecipe.recipeImage =
                        "android.resource://com.TheCooker/${R.drawable.testmeal}"
                    _updateState.value = _updateState.value?.copy(uploadingImage = false)

                }




                try {
                    recipeRepo.updateRecipe(cleanedRecipe)
                }catch (e: Exception){
                    Log.d("RecipeViewModel", "Error updating recipe: ${e.message}")
                }
                Log.d("SaveStateValueBefore", "${_updateState.value}")

                _updateState.value = (_updateState.value?.copy(
                    isSaving = false,
                    error = null,
                    saveSuccess = true,
                    uploadingImage = false,
                    imageUploaded = true
                ) ?: updateRecipe())

                onSuccess()


                Log.d("SaveStateValueAfter", "${_updateState.value}")
            } catch (e: Exception) {
                _updateState.postValue(
                    _updateState.value?.copy(
                        isSaving = false,
                        error = "Error occurred check Image ${e.message}",
                        saveSuccess = false,
                        uploadingImage = false,
                        imageUploaded = false
                    )
                )
                Log.e("SaveRecipeError", "Error saving recipe: ${e.message}", e)
            }
        }
        return cleanedRecipe
    }








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
    fun resetState() {
        _mealsDetailState.value = MealsDetailState(loading = false, list = emptyList(), error = null)
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
