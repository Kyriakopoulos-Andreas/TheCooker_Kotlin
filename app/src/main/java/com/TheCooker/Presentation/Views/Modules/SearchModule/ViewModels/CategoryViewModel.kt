package com.TheCooker.Presentation.Views.Modules.SearchModule.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.Domain.Layer.Models.RecipeModels.CategoryModel
import com.TheCooker.Domain.Layer.Repositories.RecipeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val recipeRepo: RecipeRepo
): ViewModel() {

    data class RecipeState(
        val error: String? = null,
        val loading: Boolean = false,
        val list: List<CategoryModel> = emptyList()
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