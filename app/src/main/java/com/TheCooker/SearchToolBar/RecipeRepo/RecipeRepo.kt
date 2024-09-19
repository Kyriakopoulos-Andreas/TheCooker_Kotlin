package com.TheCooker.SearchToolBar.RecipeRepo

import android.net.Uri
import android.util.Log
import com.TheCooker.Login.SignIn.UserData
import com.TheCooker.Login.SignIn.UserDataProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class RecipeRepo@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userData: UserDataProvider

) {

    // RECIPES
    suspend fun saveRecipe(recipe: UserRecipe) {
        firestore.collection("recipes")
            .document(recipe.recipeId ?: "")
            .set(recipe)
            .await()
    }

    suspend fun uploadImageAndGetUrl(imageUri: Uri): String? {
        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("recipes/images/${UUID.randomUUID()}.jpg")
            val uploadTask = imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getRecipes(categoryId: String): List<UserRecipe> {
        return try {
            Log.d("RecipeRepo", "CreatorId: ${userData.userData?.uid}")
            val querySnapshot = firestore.collection("recipes")
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("creatorId", userData.userData?.uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener { e -> // Διαχειριζομαι τα σφαλαματα του firestore
                    Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
                }
                .await()

            val recipes = querySnapshot.toObjects(UserRecipe::class.java)
            Log.d("RecipeRepo", "Fetched recipes: $recipes")
            recipes
        } catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
            emptyList()
        }
    }

    suspend fun deleteRecipe(recipeId: String){
        firestore.collection("recipes")
            .document(recipeId)
            .delete()
            .await()
    }

    suspend fun updateRecipe(recipe: UserRecipe){
        firestore.collection("recipes")
            .document(recipe.recipeId?: "")
            .set(recipe, SetOptions.merge())
            .await()
    }

    //CATEGORIES
    suspend fun saveCategory(category: Category) {
        firestore.collection("categories")
            .document(category.idCategory?: "")
            .set(category)
            .await()

    }

    suspend fun getCategories(): List<Category>{
        return firestore.collection("categories").get().await().toObjects(Category::class.java)
    }

    suspend fun deleteCategory(categoryId:String){
        firestore.collection("categories").document(categoryId).delete().await()
    }

    suspend fun updateCategory(category: Category){
        firestore.collection("categories").document(category.idCategory?: "").
        set(category, SetOptions.merge()).await()
    }


    suspend fun syncApiCategoriesWithFirebase(apiCategories: List<Category>){
        val localCategories = getCategories()
        val localCategoryNames = localCategories.map{it.strCategory}.toSet()
        val newCategories = apiCategories.filter { it.strCategory !in localCategoryNames }

        newCategories.forEach{category ->
            val newId = generateCategoryId(category.strCategory?: "")
            saveCategory(category.copy(idCategory = newId))
        }
    }

    private fun generateCategoryId(name: String): String {
        return name.hashCode().toString()
    }


}