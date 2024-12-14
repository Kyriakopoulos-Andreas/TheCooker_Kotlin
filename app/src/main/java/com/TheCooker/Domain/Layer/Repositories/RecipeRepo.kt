package com.TheCooker.Domain.Layer.Repositories

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.TheCooker.CookerApp
import com.TheCooker.Domain.Layer.Models.RecipeModels.ApiMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.CategoryModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealModel
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserResponse
import com.TheCooker.dataLayer.Api.ApiService
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
    private val apiService: ApiService,
    @ApplicationContext private val context: Context

) {

    // RECIPES

    suspend fun getDetails(id: String): UserResponse {
        return try {
            val response = firestore.collection("recipes")
                .whereEqualTo("name", id)
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
    private fun DocumentSnapshot.toUserRecipe(): UserMealDetailModel? {
        return try {
            UserMealDetailModel(
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


    suspend fun saveRecipe(recipe: UserMealDetailModel) {
        firestore.collection("recipes")
            .document(recipe.recipeId ?: "")
            .set(recipe)
            .await()
    }



    suspend fun uploadImageAndGetUrl(imageUri: Uri): String? {
        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("recipes/images/${UUID.randomUUID()}.jpg")

            // Ανέβασμα της εικόνας
            imageRef.putFile(imageUri).await()

            // Λήψη του download URL
            val downloadUrl = imageRef.downloadUrl.await().toString()

            // Προσθήκη Log για να δεις το επιστρεφόμενο URL
            Log.d("ImageDownloadUrl", "Uploaded Image URL: $downloadUrl")

            // Επιστροφή του URL
            downloadUrl
        } catch (e: Exception) {
            Log.e("UploadImageError", "Error uploading image: ${e.message}", e)
            null
        }
    }

    suspend fun getUserRecipeDetails(recipeId: String): List<UserMealDetailModel>{
        return try{
            val querySnapshot = firestore.collection("recipes")
                .whereEqualTo("recipeId", recipeId)
                .get()
                .addOnFailureListener { e -> // Διαχειριζομαι τα σφαλαματα του firestore
                    Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
                }
                .await()

            val recipes = querySnapshot.toObjects(UserMealDetailModel::class.java)
            recipes
        }catch (e:Exception){
            Log.d("RecipeRepo", "Error fetching recipes: ${e.message}")
            throw e.cause ?: e
        }
    }



    suspend fun getRecipes(categoryId: String): List<UserMealDetailModel> {
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

            val recipes = querySnapshot.toObjects(UserMealDetailModel::class.java)
            Log.d("RecipeRepo", "Fetched recipes: $recipes")
            recipes
        } catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
            emptyList()
        }
    }

    suspend fun getApiRecipesFromFirestore(categoryId: String): List<UserMealModel> {
        return try {
            val querySnapshot = firestore.collection("recipesFromApi")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            // Καταγραφή των εγγράφων στο log
            querySnapshot.documents.forEach { document ->
                Log.d("queryDB", "Fetched recipe document: ${document.data}")
            }

           val recipes =  querySnapshot.toObjects(UserMealModel::class.java)
            recipes
        } catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
            emptyList()
        }
    }



    suspend fun syncApiMealsWithFirebase(
        categoryId: String,
        apiMeals: List<UserMealModel>,
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
            val response = apiService.getMealDetail(meal.strMeal)
            val details = response.meals

            // Αποθήκευση των λεπτομερειών του γεύματος
            syncApiMealDetailsWithFirebase(meal.idMeal, details)
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


    suspend fun saveApiMeals(meal: UserMealModel, categoryId: String) {
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

    suspend fun updateRecipe(recipe: UserMealDetailModel){
        val dataToUpdateBaseOnMap = recipe.mapForUpdateExcludingCreatorId()
        firestore.collection("recipes")
            .document(recipe.recipeId?: "")
            .set(dataToUpdateBaseOnMap, SetOptions.merge())
            .await()
    }

    //CATEGORIES
    suspend fun saveCategory(categoryModel: CategoryModel) {

        firestore.collection("categories")
            .document(categoryModel.idCategory?: "")
            .set(categoryModel)
            .await()

    }





    suspend fun getCategories(): List<CategoryModel>{
        return firestore.collection("categories").get().await().toObjects(CategoryModel::class.java)
    }

    suspend fun deleteCategory(categoryId:String){
        firestore.collection("categories").document(categoryId).delete().await()
    }

    suspend fun updateCategory(categoryModel: CategoryModel){
        firestore.collection("categories").document(categoryModel.idCategory?: "").
        set(categoryModel, SetOptions.merge()).await()
    }


    suspend fun syncApiCategoriesWithFirebase(apiCategories: List<CategoryModel>){
        val localCategories = getCategories()
        val localCategoryNames = localCategories.map{it.strCategory}.toSet()
        val newCategories = apiCategories.filter { it.strCategory !in localCategoryNames }

        newCategories.forEach{category ->
            val newId = generateId(category.strCategory?: "")
            saveCategory(category.copy(idCategory = newId))
        }
    }

    // MEAL DETAILS
    suspend fun saveMealDetails(meal: ApiMealDetailModel){
        firestore.collection("mealDetails")
            .document(meal.idMeal?: "") // To document προσδιορίζει το ΙD του εγγραφου στο firestore
            .set(meal)
            .await()

    }

    suspend fun getApiDetailsFromFirestore(mealId: String): List<ApiMealDetailModel>{
        return try {
            Log.d("mealIdCheck", "Fetching recipes for mealId: $mealId")

            val querySnapshot = firestore.collection("mealDetails")
                .whereEqualTo("idMeal", mealId)
                .get()
                .await()
            val detail =  querySnapshot.toObjects(ApiMealDetailModel::class.java)
            detail
        }catch (e: Exception){
            Log.e("RecipeDetails", "Error fetching recipes: ${e.message}")
            emptyList()
        }
    }

    suspend fun syncApiMealDetailsWithFirebase(mealId: String, apiMealDetailModels: List<ApiMealDetailModel>) {
        val localMealDetails = getApiDetailsFromFirestore(mealId)
        val localMealDetailIds = localMealDetails.map { it.idMeal }.toSet()

        // Φιλτράρισμα των νέων λεπτομερειών που δεν υπάρχουν στις τοπικές λεπτομέρειες
        val newMealDetails = apiMealDetailModels.filter { it.idMeal !in localMealDetailIds }

        Log.d("NewMealDetailsCount", "Found ${newMealDetails.size} new meal details to sync for mealId: $mealId")

        // Αποθήκευση των νέων λεπτομερειών στη βάση δεδομένων
        newMealDetails.forEach { detail ->
            Log.d("SavingMealDetail", "Saving meal detail with ID: ${detail.idMeal} for mealId: $mealId")
            saveMealDetails(detail)
        }
    }

}