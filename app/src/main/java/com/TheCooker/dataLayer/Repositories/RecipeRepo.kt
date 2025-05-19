package com.TheCooker.dataLayer.Repositories

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Settings
import android.util.Log
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.Common.Layer.Resources.uploadDownloadResourceWithPagination
import com.TheCooker.CookerApp
import com.TheCooker.Domain.Layer.Models.RecipeModels.ApiMealDetailModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.CategoryModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealModel
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.PostCommentModel
import com.TheCooker.Domain.Layer.Models.RecipeModels.UserMealDetailModel
import com.TheCooker.dataLayer.Api.ApiService
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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



    suspend fun fetchRandomSharesForHomeView(): uploadDownloadResource<List<UserMealDetailModel>> {
        return try {
            val querySnapshot = firestore.collection("recipes")
                .whereEqualTo("visibility", true)
                .whereNotEqualTo("creatorId", userData.userData?.email)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnFailureListener { e ->
                    Log.e("RecipeRepo", "Error fetching recipe shares for HomeView: ${e.message}")
                }
                .await()

            Log.d("RecipeRepo", "Fetched random recipes for HomeView: $querySnapshot")

            val recipes = querySnapshot.documents.mapNotNull { document ->
                document.data ?: return@mapNotNull null

                document.toUserRecipe()
            }

            Log.d("RecipeRepo", "Parsed recipe list: $recipes")

            uploadDownloadResource.Success(recipes)

        } catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching random recipes for HomeView: ${e.message}")
            uploadDownloadResource.Error(e)
        }
    }

    suspend fun updateComment(comment: PostCommentModel): uploadDownloadResource<Unit> {
        return try {
            Log.d("RecipeRepo", "Trying to update comment: $comment")
            firestore.collection("Comments")
                .document(comment.commentId)
                .set(comment)
                .await()
            uploadDownloadResource.Success(Unit)
            }catch (e: Exception){
            uploadDownloadResource.Error(e)
        }
    }

    suspend fun likePost(share: UserMealDetailModel, userId: String): uploadDownloadResource<Unit> {
        return try {
            firestore.collection("recipes")
                .document(share.recipeId ?: "")
                .update("whoLikeIt", FieldValue.arrayUnion(userId)).await()
            firestore.collection("recipes")
                .document(share.recipeId ?: "")
                .update("countLikes", FieldValue.increment(1)).await()
            uploadDownloadResource.Success(Unit)
        }catch(e: Exception){
            uploadDownloadResource.Error(e)
        }
    }

    suspend fun unLikePost(share: UserMealDetailModel, user: String): uploadDownloadResource<Unit> {
        return try {
            firestore.collection("recipes")
                .document(share.recipeId ?: "")
                .update("whoLikeIt", FieldValue.arrayRemove(user)).await()  // πέρασε το user ολόκληρο εδώ

            firestore.collection("recipes")
                .document(share.recipeId ?: "")
                .update("countLikes", FieldValue.increment(-1)).await()

            uploadDownloadResource.Success(Unit)
        } catch (e: Exception) {
            uploadDownloadResource.Error(e)
        }
    }



    suspend fun deleteComment(comment: String): uploadDownloadResource<Unit> {
        return try {
            firestore.collection("Comments")
                .document(comment)
                .delete()
                .await()
            uploadDownloadResource.Success(Unit)
        }catch (e: Exception){
            uploadDownloadResource.Error(e)
        }
    }


    suspend fun fetchUsersWhoLikedSpecificCommentWithPagination(
        comment: PostCommentModel,  // Το σχόλιο για το οποίο θέλουμε να φορτώσουμε τους χρήστες που το έχουν αρέσει
        startIndex: Int,            // Ο δείκτης από τον οποίο θα ξεκινήσει η φόρτωση
        pageSize: Int = 10         // Μέγεθος της σελίδας (πόσοι χρήστες να φορτωθούν κάθε φορά, προεπιλεγμένα 10)
    ): uploadDownloadResourceWithPagination<List<UserDataModel>> {
        return try {
            val whoLiked = comment.whoLikeIt ?: emptyList()  // Παίρνουμε τη λίστα των χρηστών που άρεσαν το σχόλιο ή κενή λίστα αν δεν υπάρχουν

            // Αν ο δείκτης υπερβαίνει το μέγεθος της λίστας, σημαίνει ότι δεν υπάρχουν άλλοι χρήστες για να φορτωθούν
            if (startIndex >= whoLiked.size) {
                return uploadDownloadResourceWithPagination.Success(emptyList(), null)  // Επιστρέφουμε άδεια λίστα με null ως δείκτη για το επόμενο σετ
            }

            // Υπολογισμός του δείκτη τέλους της σελίδας
            // Παίρνουμε το επόμενο κομμάτι των χρηστών που θα φορτωθούν (ξεκινώντας από το startIndex και μέχρι το endIndex)
            val endIndex = (startIndex + pageSize).coerceAtMost(whoLiked.size)  // Διασφαλίζουμε ότι το endIndex δεν ξεπερνά το μέγεθος της λίστας

            // Παίρνουμε τους UIDs των χρηστών από την υπολίστα του whoLiked
            val currentPageUids = whoLiked.subList(startIndex, endIndex)

            // Κάνουμε το query στη βάση δεδομένων για να πάρουμε τους χρήστες που αντιστοιχούν στους UIDs
            val userSnapshot = firestore.collection("users")
                .whereIn("uid", currentPageUids)  // Επιλέγουμε χρήστες με τα UIDs που έχουμε
                .get()
                .await()  // Περιμένουμε να τελειώσει το αίτημα

            // Μετατρέπουμε τα έγγραφα της βάσης σε αντικείμενα UserDataModel
            val userList = userSnapshot.documents.mapNotNull { it.toObject(UserDataModel::class.java) }

            // Αν δεν υπάρχουν άλλοι χρήστες, επιστρέφουμε άδειο και το νέο startIndex ως null (δεν υπάρχουν άλλα δεδομένα)
            if (userList.isEmpty()) {
                uploadDownloadResourceWithPagination.Success(emptyList(), null)
            } else {
                // Αν έχουμε δεδομένα, επιστρέφουμε την λίστα χρηστών και το νέο startIndex για την επόμενη σελίδα
                // Ο νέος startIndex είναι το τέλος του τρέχοντος κομματιού
                val newStartIndex = endIndex  // Ο νέος δείκτης ξεκινάει από το επόμενο σημείο
                uploadDownloadResourceWithPagination.Success(userList, newStartIndex)  // Επιστρέφουμε την λίστα χρηστών και τον νέο δείκτη
            }
        } catch (e: Exception) {
            // Αν συμβεί κάποιο λάθος κατά την κλήση της βάσης ή τη μετατροπή, επιστρέφουμε το σφάλμα
            uploadDownloadResourceWithPagination.Error(e)
        }
    }


    suspend fun fetchLikesForPostWithPagination(
        share: UserMealDetailModel?,
        startIndex: Int,
        pageSize: Int = 10
    ): uploadDownloadResourceWithPagination<List<UserDataModel>> {
        return try {
            val whoLiked = share?.whoLikeIt ?: emptyList()

            // Αν ο δείκτης υπερβαίνει το μέγεθος της λίστας, σημαίνει ότι δεν υπάρχουν άλλοι χρήστες για να φορτωθούν
            if (startIndex >= whoLiked.size) {
                return uploadDownloadResourceWithPagination.Success(emptyList(), null)  // Επιστρέφουμε άδεια λίστα με null ως δείκτη για το επόμενο σετ
            }

            // Υπολογισμός του δείκτη τέλους της σελίδας
            // Παίρνουμε το επόμενο κομμάτι των χρηστών που θα φορτωθούν (ξεκινώντας από το startIndex και μέχρι το endIndex)
            val endIndex = (startIndex + pageSize).coerceAtMost(whoLiked.size)  // Διασφαλίζουμε ότι το endIndex δεν ξεπερνά το μέγεθος της λίστας

            // Παίρνουμε τους UIDs των χρηστών από την υπολίστα του whoLiked
            val currentPageUids = whoLiked.subList(startIndex, endIndex)

            // Κάνουμε το query στη βάση δεδομένων για να πάρουμε τους χρήστες που αντιστοιχούν στους UIDs
            val userSnapshot = firestore.collection("users")
                .whereIn("uid", currentPageUids)  // Επιλέγουμε χρήστες με τα UIDs που έχουμε
                .get()
                .await()  // Περιμένουμε να τελειώσει το αίτημα

            // Μετατρέπουμε τα έγγραφα της βάσης σε αντικείμενα UserDataModel
            val userList = userSnapshot.documents.mapNotNull { it.toObject(UserDataModel::class.java) }

            // Αν δεν υπάρχουν άλλοι χρήστες, επιστρέφουμε άδειο και το νέο startIndex ως null (δεν υπάρχουν άλλα δεδομένα)
            if (userList.isEmpty()) {
                uploadDownloadResourceWithPagination.Success(emptyList(), null)
            } else {
                // Αν έχουμε δεδομένα, επιστρέφουμε την λίστα χρηστών και το νέο startIndex για την επόμενη σελίδα
                // Ο νέος startIndex είναι το τέλος του τρέχοντος κομματιού
                val newStartIndex = endIndex  // Ο νέος δείκτης ξεκινάει από το επόμενο σημείο
                uploadDownloadResourceWithPagination.Success(userList, newStartIndex)  // Επιστρέφουμε την λίστα χρηστών και τον νέο δείκτη
            }
        } catch (e: Exception) {
            // Αν συμβεί κάποιο λάθος κατά την κλήση της βάσης ή τη μετατροπή, επιστρέφουμε το σφάλμα
            uploadDownloadResourceWithPagination.Error(e)
        }
    }





    suspend fun unLikeComment(comment: PostCommentModel): uploadDownloadResource<Unit> {
        return try {
            firestore.collection("Comments")
                .document(comment.commentId)
                .update("whoLikeIt", FieldValue.arrayRemove(userData.userData?.uid)).await()

            firestore.collection("Comments")
                .document(comment.commentId)
                .update("countLikes", FieldValue.increment(-1)).await()

            uploadDownloadResource.Success(Unit)

        }catch (e: Exception){
            Log.e("RecipeRepo", "Error unliking comment: ${e.message}")
            uploadDownloadResource.Error(e)
        }

    }


    suspend fun commentLike(comment: PostCommentModel, userId: String): uploadDownloadResource<Unit> {
        return try {
            firestore.collection("Comments")
                .document(comment.commentId)
                .update("whoLikeIt", FieldValue.arrayUnion(userId)).await()

            firestore.collection("Comments")
                .document(comment.commentId)
                .update("countLikes", FieldValue.increment(1)).await()

            uploadDownloadResource.Success(Unit)
        }catch (e: Exception){
            uploadDownloadResource.Error(e)
        }
    }



    suspend fun createPostComment(comment: PostCommentModel): uploadDownloadResource<Unit> {
        return try {
            Log.d("RecipeRepo", "Trying to create post comment: $comment")
            firestore.collection("Comments")
                .document(comment.commentId)
                .set(comment)
                .await()

            firestore.collection("recipes")
                .document(comment.postId)
                .update("countComments", FieldValue.increment(1))
                .await()

            uploadDownloadResource.Success(Unit)
        }catch (e: Exception){
            Log.e("RecipeRepo", "Firestore exception: ${e.message}", e)
            uploadDownloadResource.Error(e)
        }
    }

    suspend fun fetchComments(shares: List<UserMealDetailModel>): uploadDownloadResource<List<PostCommentModel>> {
        return try {
            val allComments = mutableListOf<PostCommentModel>()
            val shareIds = shares.mapNotNull { it.recipeId }

            for (chunk in shareIds.chunked(10)) {
                val querySnapshot = firestore.collection("Comments")
                    .whereIn("postId", chunk)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val comments = querySnapshot.documents.mapNotNull { doc ->
                    val comment = doc.toObject(PostCommentModel::class.java)
                    comment?.apply {
                        commentId = doc.id
                        whoLikeIt = doc.get("whoLikeIt") as? MutableList<String> ?: mutableListOf()
                    }
                }

                allComments += comments
            }


            uploadDownloadResource.Success(allComments)

        } catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching comments: ${e.message}")
            uploadDownloadResource.Error(e)
        }
    }



     fun listenToCommentCount(recipeId: String, onChange: (Int) -> Unit): ListenerRegistration {
        return firestore.collection("recipes")
            .document(recipeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val count = snapshot.getLong("countComments")?.toInt() ?: 0
                onChange(count)
            }
    }

    fun listenToPostLikesCount(recipeId: String, onChange: (Int) -> Unit): ListenerRegistration {
        return firestore.collection("recipes")
            .document(recipeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val count = snapshot.getLong("countLikes")?.toInt() ?: 0
                onChange(count)
            }
    }

    fun listenToCommentsForPost(
        recipeId: String,
        onChange: (List<PostCommentModel>) -> Unit
    ): ListenerRegistration {
        return firestore.collection("Comments")
            .whereEqualTo("postId", recipeId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val comments = snapshot.toObjects(PostCommentModel::class.java)
                onChange(comments)
            }
    }






//    suspend fun getDetails(id: String): UserResponse {
//        return try {
//            val response = firestore.collection("recipes")
//                .whereEqualTo("name", id)
//                .get()
//                .await()
//
//
//            val recipe = if (response.documents.isNotEmpty()) {
//                response.documents[0].toUserRecipe()
//            } else {
//                null
//            }
//
//            UserResponse(userMeal = recipe)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            UserResponse(userMeal = null)
//        }
//    }

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
                timestamp = getLong("timestamp") ?: System.currentTimeMillis(),
                countComments = getLong("countComments")?.toInt() ?: 0,
                countLikes = getLong("countLikes")?.toInt() ?: 0,
                whoLikeIt = get("whoLikeIt") as? List<String>
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

            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()


            Log.d("ImageDownloadUrl", "Uploaded Image URL: $downloadUrl")


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

    suspend fun fetchRecipeShares(): uploadDownloadResource<List<UserMealDetailModel>> {
        return try {
            Log.d("RecipeRepo", "Trying to fetch recipe shares")
            val querySnapshot = firestore.collection("recipes")
                .whereEqualTo("creatorId", userData.userData?.uid)
                .whereEqualTo("visibility", true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener { e ->
                    Log.e("RecipeRepo", "Error fetching recipe shares: ${e.message}")
                }
                .await()
            val shares = querySnapshot.toObjects(UserMealDetailModel::class.java)
            Log.d("RecipeRepo", "Fetched recipe shares: $shares")
            uploadDownloadResource.Success(shares)
        }catch (e: Exception) {
            Log.e("RecipeRepo", "Error fetching recipes: ${e.message}")
            uploadDownloadResource.Error(e)

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
            .document(meal.idMeal)
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

//    suspend fun deleteCategory(categoryId:String){
//        firestore.collection("categories").document(categoryId).delete().await()
//    }
//
//    suspend fun updateCategory(categoryModel: CategoryModel){
//        firestore.collection("categories").document(categoryModel.idCategory?: "").
//        set(categoryModel, SetOptions.merge()).await()
//    }


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
            .document(meal.idMeal) // To document προσδιορίζει το ΙD του εγγραφου στο firestore
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

    suspend fun deletePost(post: UserMealDetailModel?): uploadDownloadResource<Unit>{
        return try {
            if (post != null) {
                firestore.collection("recipes")
                    .document(post.recipeId ?: "")
                    .update("visibility", false)
            } // Ενημερώνεις το πεδίο "visibility" σε false
            uploadDownloadResource.Success(Unit)
        }catch (e: Exception){
            uploadDownloadResource.Error(e)
        }
    }

}