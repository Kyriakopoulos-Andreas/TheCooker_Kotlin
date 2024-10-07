package com.TheCooker.SearchToolBar.ViewModels


import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.Login.SignIn.UserDataProvider
import com.TheCooker.R
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
import com.TheCooker.SearchToolBar.ApiService.UserRecipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateMealViewModel @Inject constructor(
    private val userDataProvider: UserDataProvider,
    private val recipeRepo: RecipeRepo,

): ViewModel()  {

    data class newRecipe(
        val error: String? = null,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false,
        val uploadingImage: Boolean = false,
        var onCreateRecipe: Boolean = false,
        val imageUploaded: Boolean = false
    )





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

    fun onCreateTrue(){
        _saveState.value = _saveState.value?.copy(onCreateRecipe = true)
    }

    fun onCreateFalse(){
        _saveState.value = _saveState.value?.copy(onCreateRecipe = false)
    }

    //MealName
    fun onMealNameChange(newValue: String) {
        _mealName.value = newValue
    }
    //Ingredients
    fun addIngredientAfter(index: Int){
        _ingredients.add(index + 1, "")

    }

    fun addIngredientAtEnd(){
        _ingredients.add("")
    }

    fun updateIngredient(index: Int, newValue: String){
        _ingredients[index] = newValue
    }


    fun removeIngredient(index: Int){
        if(_ingredients.size > 2) {
            _ingredients.removeAt(index)
        }
    }

    //Steps

    fun addStepAfter(index: Int){
        _steps.add(index + 1, "")
    }

    fun addStepAtTheEnd(){
        _steps.add("")
    }

    fun updateSteps(index: Int, newValue: String){
        _steps[index] = newValue

    }


    fun removeStep(index: Int){
        if(_steps.size > 2) {
            _steps.removeAt(index)
        }
    }

     fun saveRecipe(recipe: UserRecipe, imageUri: Uri?, onSuccess: () -> Unit) {
         _saveState.value = _saveState.value?.copy(isSaving = true)

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
                    recipe.recipeImage = downloadUrl
                    Log.d("TestDownLoadUrl", "Image URL: ${recipe.recipeImage}")

                    Log.d("SaveStateValueImageUploaded", "${_saveState.value?.imageUploaded}")
                } else {
                    recipe.recipeImage = "android.resource://com.TheCooker/${R.drawable.testmeal}"
                    _saveState.value = _saveState.value?.copy(uploadingImage = false)

                }

                Log.d("RecipeData", "Saving recipe: $recipe")

                recipeRepo.saveRecipe(recipe)
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
    }
}