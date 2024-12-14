package com.TheCooker.Domain.Layer.Repositories

import android.util.Log
import com.TheCooker.Common.Layer.Resources.CreatePasswordResource
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepo@Inject constructor(
    val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,

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
            Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!", "uid")

            // Προσθήκη καταγραφής
            println("User created with email: $email")

            if(uid != null) {
                // Προσθήκη χρήστη στο Firestore
                val user = UserDataModel(
                    uid = uid ?:"",
                    userName = "$firstName $lastName",
                    password = password,
                    email = email,
                    profilerPictureUrl = profilePictureUrl
                )

                saveUserToFirestore(user = user)
            }else{
                CreatePasswordResource.Error(Exception("Failed to create user"))
            }

            CreatePasswordResource.Success(true)
        } catch (e: Exception) {
            // Προσθήκη καταγραφής
            println("Error during signUp: ${e.message}")
            CreatePasswordResource.Error(e)
        }
    }

    suspend fun saveUserToFirestore(user: UserDataModel) {
        firestore.collection("users").document(user.email.toString()).set(user).await()
        // Προσθήκη καταγραφής
        println("User saved to Firestore with email: ${user.email}")
    }

    suspend fun login(email: String, password: String): LoginResults<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            LoginResults.Success(true)
        } catch (e: Exception) {
            LoginResults.Error(e)
        }


    suspend fun getUserDetails(email: String): LoginResults<UserDataModel> {
        return try {
            val querySnapshot = firestore.collection("users").whereEqualTo("email", email).get().await()
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


}