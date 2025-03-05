package com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.Domain.Layer.Models.RecipeModels.ApiMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.dataLayer.Repositories.RecipeRepo
import com.TheCooker.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
        data class ApiMealDetail(val apiMealDetailModel: List<ApiMealDetailModel>): recipeDetails()
        data class UserMealDetail(val mealDetail: List<UserMealDetailModel>): recipeDetails()
    }

    data class MealsDetailState(
        val loading: Boolean = false,
        val error: String? = null,
        val list: List<recipeDetails
                > = emptyList()
    )
    private val _mealsDetailState = MutableLiveData(MealsDetailState())
    val mealsDetailState: LiveData<MealsDetailState> = _mealsDetailState

    fun copyDetails(details: UserMealDetailModel, updateDetails: UserMealDetailModel?): UserMealDetailModel {
        return copyObj(details, updateDetails)
    }
    fun setDetailsForPost(details: UserMealDetailModel){
        _mealsDetailState.value = _mealsDetailState.value?.copy(list = listOf(recipeDetails.UserMealDetail(listOf(details))))
    }

    private fun copyObj(details: UserMealDetailModel, updateDetails: UserMealDetailModel?): UserMealDetailModel {
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

    private val _updatePost = MutableStateFlow<Boolean>(false)
    val updatePost : MutableStateFlow<Boolean> get() = _updatePost

    private val _updateState = MutableLiveData(updateRecipe())
    val updateState: LiveData<updateRecipe> = _updateState

    private val _updateButtonDisabled = mutableStateOf(true)
    val updateDetailsButtonDisabled: MutableState<Boolean> get() = _updateButtonDisabled

    fun updatePostState(value: Boolean) {
        _updatePost.value = value
    }

    fun onUpdateTrue() {
        _updateState.value = _updateState.value?.copy(onUpdateRecipe = true)
    }

    fun onUpdateFalse() {
        _updateState.value = _updateState.value?.copy(onUpdateRecipe = false)
    }


    fun setUpdateSaveButtonDisabled() {
        _updateButtonDisabled.value = false
    }

    fun setUpdateSaveButtonEnabled(){
        _updateButtonDisabled.value = true
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







    suspend fun updateRecipe(recipe: UserMealDetailModel, imageUri: Uri?, onSuccess: () -> Unit): UserMealDetailModel {
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

    fun resetUpdateStates(){
        updatedMealNameError.value = ""
        _updatedIngredientsError.value = ""
        _updatedMealName.value = ""
        _updatedStepsError.value = ""
        _updatedStateOfCompletion.value = false
        _updatedSteps.clear()
        _updatedSteps.add(0, "")
        _updatedSteps.add(1, "")
        _updatedIngredients.clear()
        _updatedIngredients.add(0, "")
        _updatedIngredients.add(1, "")
        _updateButtonDisabled.value = true
    }
}