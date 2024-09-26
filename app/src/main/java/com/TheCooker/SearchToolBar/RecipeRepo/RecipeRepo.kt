package com.TheCooker.SearchToolBar.RecipeRepo

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.TheCooker.CookerApp
import com.TheCooker.CookerApp.Companion.ADMIN_DEVICE_ID
import com.TheCooker.Login.SignIn.UserDataProvider
import com.TheCooker.SearchToolBar.ApiService.UserRecipe
import com.TheCooker.SearchToolBar.ApiService.UserResponse
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject


class RecipeRepo@Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userData: UserDataProvider,
    @ApplicationContext private val context: Context

) {

    // RECIPES

    suspend fun getDetails(meal: String): UserResponse {
        return try {
            val response = firestore.collection("recipes")
                .whereEqualTo("name", meal)
                .get()
                .await()

            // Ελέγχει αν υπάρχει κάποιο έγγραφο στο αποτέλεσμα
            val recipe = if (response.documents.isNotEmpty()) {
                response.documents[0].toUserRecipe()
            } else {
                null
            }

            UserResponse(userMeal = recipe)
        } catch (e: Exception) {
            e.printStackTrace()
            // Επιστρέφει έναν UserResponse με null αν συμβεί σφάλμα
            UserResponse(userMeal = null)
        }
    }

    // Extension function για τη μετατροπή DocumentSnapshot σε UserRecipe
    private fun DocumentSnapshot.toUserRecipe(): UserRecipe? {
        return try {
            UserRecipe(
                categoryId = getString("categoryId"),
                recipeId = getString("recipeId"),
                recipeName = getString("recipeName"),
                recipeIngredients = get("recipeIngredients") as? List<String>,
                steps = get("steps") as? List<String>,
                recipeImage = getString("recipeImage"),
                creatorId = getString("creatorId"),
                timestamp = getLong("timestamp") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


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

    suspend fun getApiRecipesFromFirestore(categoryId: String): List<MealsCategory> {
        return try {
            val querySnapshot = firestore.collection("recipesFromApi")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            // Καταγραφή των εγγράφων στο log
            querySnapshot.documents.forEach { document ->
                Log.d("queryDB", "Fetched recipe document: ${document.data}")
            }

           val recipes =  querySnapshot.toObjects(MealsCategory::class.java)
            recipes
        } catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
            emptyList()
        }
    }



    suspend fun syncApiMealsWithFirebase(
        categoryId: String,
        apiMeals: List<MealsCategory>,
    ) {
        if (!checkIfAdmin()) {
            Log.d("RecipeRepo", "User is not admin, skipping sync")
            return
        }

        // Λήψη των τοπικών γευμάτων από τη βάση δεδομένων για την καθορισμένη κατηγορία
        val localMeals = getApiRecipesFromFirestore(categoryId)
        Log.d("CheckForCategoryId", "categoryId: $categoryId")

        // Δημιουργία συνόλου με τα ids των τοπικών γευμάτων για σύγκριση
        val localMealIds = localMeals.map { it.idMeal }.toSet()

        // Φιλτράρισμα των νέων γευμάτων που δεν υπάρχουν στις τοπικές γεύσεις
        val newMeals = apiMeals.filter { it.idMeal !in localMealIds }

        Log.d("NewMealsCount", "Found ${newMeals.size} new meals to sync for categoryId: $categoryId")

        // Αποθήκευση των νέων γευμάτων στη βάση δεδομένων
        newMeals.forEach { meal ->
            Log.d("SavingMeal", "Saving meal with ID: ${meal.idMeal} and categoryId: ${meal.categoryId}")
            saveApiMeals(meal, categoryId)
        }
    }

    @SuppressLint("HardwareIds")
    fun checkIfAdmin(): Boolean {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentDeviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val adminDeviceId = sharedPreferences.getString(CookerApp.ADMIN_DEVICE_ID, null)

        if (adminDeviceId == null) {
            Log.d("DeviceCheck", "Admin device ID is not set.")
            return false
        }

        Log.d("DeviceCheck", "Current device ID: $currentDeviceId")
        Log.d("DeviceCheck", "Admin device ID: $adminDeviceId")

        return currentDeviceId == adminDeviceId
    }


    fun generateId(name: String): String {
        return name.hashCode().toString()
    }


    suspend fun saveApiMeals(meal: MealsCategory, categoryId: String) {
        val mealWithCategoryId = meal.copy(categoryId = categoryId) // Χρησιμοποιούμε το hash ID της κατηγορίας
        Log.d("FirestoreSave", "Storing meal ID: ${meal.idMeal} with categoryId: $categoryId")
        firestore.collection("recipesFromApi")
            .document(meal.idMeal ?: "")
            .set(mealWithCategoryId)
            .await()
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
            val newId = generateId(category.strCategory?: "")
            saveCategory(category.copy(idCategory = newId))
        }
    }







}