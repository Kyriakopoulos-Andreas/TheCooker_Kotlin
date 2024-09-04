package com.TheCooker.SearchToolBar.RecipeRepo

import com.TheCooker.Login.CrPassword.UserRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecipeRepo@Inject constructor(
    private val firestore: FirebaseFirestore

) {
    suspend fun saveRecipe(recipe: UserRecipe) {
        firestore.collection("recipes")
            .document(recipe.recipeId)
            .set(recipe)
            .await()
    }

    suspend fun getRecipes(): List<UserRecipe> {
        return firestore.collection("recipes")
            .get()
            .await()
            .toObjects(UserRecipe::class.java)
    }

    suspend fun deleteRecipe(recipeId: String){
        firestore.collection("recipes")
            .document(recipeId)
            .delete()
            .await()
    }

    suspend fun updateRecipe(recipe: UserRecipe){
        firestore.collection("recipes")
            .document(recipe.recipeId)
            .set(recipe, SetOptions.merge())
            .await()
    }
}