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
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.R
import com.TheCooker.Domain.Layer.Repositories.RecipeRepo
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateMealViewModel @Inject constructor(
    private val userDataProvider: UserDataProvider,
    private val recipeRepo: RecipeRepo,

    ): ViewModel() {

    data class newRecipe(
        val error: String? = null,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val uploadingImage: Boolean = false,
        var onCreateRecipe: Boolean = false,
        val imageUploaded: Boolean = false
    )

    //Validation
    private val _mealNameError = MutableStateFlow<String?>(null)
    val mealNameError: MutableStateFlow<String?> get() = _mealNameError
    private val _ingredientsError = MutableStateFlow<String?>(null)
    val ingredientsError: MutableStateFlow<String?> get() = _ingredientsError
    private val _stepsError = MutableStateFlow<String?>(null)
    val stepsError: MutableStateFlow<String?> get() = _stepsError
    private val _stateOfCompletion = MutableStateFlow<Boolean?>(false)
    val stateOfCompletion: MutableStateFlow<Boolean?> get() = _stateOfCompletion
    private val _saveButtonDisabled = mutableStateOf(true)
    val saveButtonDisabled: MutableState<Boolean> get() = _saveButtonDisabled




    val creatorId: String?
        get() = userDataProvider.userData?.uid


    private val _saveState = MutableLiveData(newRecipe())
    val saveState: LiveData<newRecipe> = _saveState


    private val _mealName = mutableStateOf("")
    val mealName: State<String> = _mealName

    private val _ingredients = mutableStateListOf<String>("", "")
    val ingredients: List<String> = _ingredients

    private val _steps = mutableStateListOf<String>("", "")
    val steps: List<String> = _steps

     fun resetStates(){
        _mealNameError.value = ""
        _ingredientsError.value = ""
        _stepsError.value = ""
        _stateOfCompletion.value = false
        _steps.clear()
        _steps.add(0, "")
        _steps.add(1, "")
        _ingredients.clear()
        _ingredients.add(0, "")
        _ingredients.add(1, "")
        _mealName.value = ""
         _saveButtonDisabled.value = true
    }

    fun setSaveButtonDisabled() {
        _saveButtonDisabled.value = false
    }


    fun onCreateTrue() {
        _saveState.value = _saveState.value?.copy(onCreateRecipe = true)
    }

    fun onCreateFalse() {
        _saveState.value = _saveState.value?.copy(onCreateRecipe = false)
    }

    //MealName
    fun setMealName(newValue: String) {
        _mealName.value = newValue
        _mealNameError.value = null
    }

    //Ingredients
    fun addIngredientAfter(index: Int) {
        _ingredients.add(index + 1, "")

    }

    fun addIngredientAtEnd() {
        _ingredients.add("")
    }

    fun updateIngredient(index: Int, newValue: String) {
        _ingredients[index] = newValue
        _ingredientsError.value = null
    }


    fun removeIngredient(index: Int) {
        if (_ingredients.size > 2) {
            _ingredients.removeAt(index)
        }
    }

    //Steps

    fun addStepAfter(index: Int) {
        _steps.add(index + 1, "")
    }

    fun addStepAtTheEnd() {
        _steps.add("")
    }

    fun updateSteps(index: Int, newValue: String) {
        _steps[index] = newValue
        _stepsError.value = null

    }


    fun removeStep(index: Int) {
        if (_steps.size > 2) {
            _steps.removeAt(index)
        }
    }

    fun validateTitle() {
        if (_mealName.value.isEmpty()) {
            _mealNameError.value = "Meal name cannot be empty"

        } else {
            _mealNameError.value = "✔"
        }

    }

    private fun validateIngredients() {
        var allNull = true

        for(ingredient in _ingredients){
            if(ingredient != "" ){
                allNull = false
                break

            }

        }
        if (allNull) {
            _ingredientsError.value = "Meal must have at least one ingredient"
        } else {
            _ingredientsError.value = "✔"
        }


    }

    private fun validateSteps() {

        var allNull = true
        for (step in _steps) {
            if (step != "") {
                allNull = false
                break
            }

        }
        if (allNull) {
            _stepsError.value = "Meal must have at least one step"
        } else {
            _stepsError.value = "✔"

        }
    }

    fun validateSave() {
        validateTitle()
        validateIngredients()
        validateSteps()
        _stateOfCompletion.value = _mealNameError.value == "✔" && _ingredientsError.value == "✔" && _stepsError.value == "✔"


    }









        fun saveRecipe(recipe: UserMealDetailModel, imageUri: Uri?, onSuccess: () -> Unit): UserMealDetailModel {
            _saveState.value = _saveState.value?.copy(isSaving = true)

            val cleanedIngredients = recipe.recipeIngredients?.filter { it.isNotEmpty() }
            val cleanedSteps = recipe.steps?.filter { it.isNotEmpty() }

            // Δημιουργία νέου αντικειμένου UserRecipe με τα καθαρισμένα στοιχεία
            val cleanedRecipe = recipe.copy(
                recipeIngredients = cleanedIngredients,
                steps = cleanedSteps
            )




            recipe.recipeIngredients

            Log.d("ImageUriAtSaveRecipeBeforeIf", "$imageUri")

            viewModelScope.launch {
                try {
                    if (imageUri != null) {
                        _saveState.value = _saveState.value?.copy(uploadingImage = true)


                        Log.d("SaveStateValueUploadingImage", "${_saveState.value?.uploadingImage}")

                        val downloadUrl = withContext(Dispatchers.IO) {
                            recipeRepo.uploadImageAndGetUrl(imageUri)
                        }

                        Log.d("TestDownLoadUrl1", "Image URL: $downloadUrl")
                        cleanedRecipe.recipeImage = downloadUrl
                        Log.d("TestDownLoadUrl", "Image URL: ${cleanedRecipe.recipeImage}")

                        Log.d("SaveStateValueImageUploaded", "${_saveState.value?.imageUploaded}")
                    } else {
                        cleanedRecipe.recipeImage =
                            "android.resource://com.TheCooker/${R.drawable.testmeal}"
                        _saveState.value = _saveState.value?.copy(uploadingImage = false)

                    }


                    Log.d("RecipeData", "Saving recipe: $recipe")

                    recipeRepo.saveRecipe(cleanedRecipe)
                    Log.d("SaveStateValueBefore", "${_saveState.value}")

                    _saveState.value = _saveState.value?.copy(
                        isSaving = false,
                        error = null,
                        saveSuccess = true,
                        uploadingImage = false,
                        imageUploaded = true
                    ) ?: newRecipe()

                    onSuccess()


                    Log.d("SaveStateValueAfter", "${_saveState.value}")
                } catch (e: Exception) {
                    _saveState.postValue(
                        _saveState.value?.copy(
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
    }