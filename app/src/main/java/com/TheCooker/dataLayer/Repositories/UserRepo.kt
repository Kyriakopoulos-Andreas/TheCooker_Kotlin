package com.TheCooker.dataLayer.Repositories

import android.net.Uri
import android.util.Log
import com.TheCooker.Common.Layer.Resources.CreatePasswordResource
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.Domain.Layer.UseCase.Location.LocationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepo@Inject constructor(
    val auth: FirebaseAuth,
    private val _userDataProvider: UserDataProvider,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
    ) {
    suspend fun signUp(
        firstName: String,
        password: String,
        email: String,
        lastName: String,
        profilePictureUrl: String?
    ): CreatePasswordResource<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser: FirebaseUser? = authResult.user

            val uid = firebaseUser?.uid

            if (uid != null) {
                val user = UserDataModel(
                    uid = uid,
                    userName = "$firstName $lastName",
                    password = password,
                    email = email,
                    profilePictureUrl = profilePictureUrl,
                )


                saveUserToFirestore(user = user)


            } else {
                CreatePasswordResource.Error(Exception("Failed to create user"))
            }

            CreatePasswordResource.Success(true)
        } catch (e: Exception) {
            // Προσθήκη καταγραφής
            println("Error during signUp: ${e.message}")
            CreatePasswordResource.Error(e)
        }
    }

    suspend fun updateUserLocation(location: LocationData, user: UserDataModel) {
        try {
            val userRef = firestore.collection("users").document(user.email.toString())
            userRef.update("countryFromWhichUserConnected", location.country)
            userRef.update("cityFromWhichUserConnected", location.city)
            userRef.update("connectedAddress", location.address)
        }catch (e: Exception){
            Log.d("UserRepo", "Error updating user location: ${e.message}")
        }
    }


    suspend fun saveUserToFirestore(user: UserDataModel) {
        firestore.collection("users").document(user.email.toString()).set(user).await()
    }

    suspend fun login(email: String, password: String): LoginResults<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            getUserDetails(email)

            LoginResults.Success(true)
        } catch (e: Exception) {
            LoginResults.Error(e)
        }


    suspend fun getUserDetails(email: String): LoginResults<UserDataModel> {
        return try {
            val querySnapshot =
                firestore.collection("users").whereEqualTo("email", email).get().await()
            if (querySnapshot.isEmpty) {
                LoginResults.Error(Exception("User not found"))
            } else {
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                val user = documentSnapshot?.toObject(UserDataModel::class.java)
                if (user != null) {
                    LoginResults.Success(user)
                } else {
                    LoginResults.Error(Exception("User object is null"))
                }
            }
        } catch (e: Exception) {
            LoginResults.Error(e)
        }
    }

    suspend fun checkIfUserExistsInFirestore(email: String): Boolean {
        val querySnapshot = firestore.collection("users").whereEqualTo("email", email).get().await()
        return !querySnapshot.isEmpty

    }

    suspend fun uploadProfilePicture(imageUri: String): uploadDownloadResource<Unit>{
        return try {
            _userDataProvider.userData?.email?.let { firestore.collection("users").document(it).update("profilePictureUrl", imageUri).await() }
            uploadDownloadResource.Success(Unit)
        }catch (e: Exception){
            uploadDownloadResource.Error(e)
        }
    }

    suspend fun uploadBackgroundPicture(imageUri: String): uploadDownloadResource<Unit>{
        return try {
            _userDataProvider.userData?.email?.let { firestore.collection("users").document(it).update("backGroundPictureUrl", imageUri).await() }
            uploadDownloadResource.Success(Unit)
        }catch (e: Exception){
            uploadDownloadResource.Error(e)
        }
    }




    suspend fun uploadImageAndGetUrl(imageUri: Uri, type: String): String? {
        return try {
            val storageRef = storage.reference
            val imageRef = if(type == "profile") {
                storageRef.child("profilePictures/Profile/${_userDataProvider.userData?.email}.jpg")
            }else{
                storageRef.child("profilePictures/BackGround/${_userDataProvider.userData?.email}.jpg")
            }
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




    suspend fun saveUserInformation(user: UserDataProvider): uploadDownloadResource<Unit> {
        val userData = user.userData
        return if (userData != null) {
            val email = userData.email
            try {
                if (email != null) {
                    firestore.collection("users")
                        .document(email) // Το email είναι το document ID
                        .set(userData) //Ανανέωση όλου του document μιας και το userData εως εδω κρατάει όλη την απαιτούμενη πληροφορια
                        .await()
                    uploadDownloadResource.Success(Unit) // Επιστροφή επιτυχίας
                } else {
                    uploadDownloadResource.Error(Exception("Email is null"))
                }
            } catch (e: Exception) {
                uploadDownloadResource.Error(e) // Επιστροφή σφάλματος
            }
        } else {
            uploadDownloadResource.Error(Exception("UserData is null"))
        }
    }
}