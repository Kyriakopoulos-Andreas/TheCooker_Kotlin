package com.TheCooker.Login.CrPassword

import com.TheCooker.Login.SignIn.CreateResults
import com.TheCooker.Login.SignIn.LoginResults
import com.TheCooker.Login.SignIn.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepo(
    internal val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signUp(
        firstName: String,
        password: String,
        email: String,
        lastName: String
    ): CreateResults<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            // Προσθήκη καταγραφής
            println("User created with email: $email")

            // Προσθήκη χρήστη στο Firestore
            val user = UserData(
                userName = "$firstName $lastName",
                password = password,
                email = email
            )

            saveUserToFirestore(user = user)

            CreateResults.Success(true)
        } catch (e: Exception) {
            // Προσθήκη καταγραφής
            println("Error during signUp: ${e.message}")
            CreateResults.Error(e)
        }
    }

    suspend fun saveUserToFirestore(user: UserData) {
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


    suspend fun getUserDetails(email: String): LoginResults<UserData> {
        return try {
            val querySnapshot = firestore.collection("users").whereEqualTo("email", email).get().await()
            if (querySnapshot.isEmpty) {
                LoginResults.Error(Exception("User not found"))
            } else {
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                val user = documentSnapshot?.toObject(UserData::class.java)
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