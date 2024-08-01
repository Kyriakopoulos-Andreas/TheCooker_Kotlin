package com.TheCooker.Login.CrPassword

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
    ): MyResult<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            // Προσθήκη καταγραφής
            println("User created with email: $email")

            // Προσθήκη χρήστη στο Firestore
            val user = User(
                firstName = firstName,
                password = password,
                email = email,
                lastName = lastName
            )
            saveUserToFirestore(user = user)

            MyResult.Success(true)
        } catch (e: Exception) {
            // Προσθήκη καταγραφής
            println("Error during signUp: ${e.message}")
            MyResult.Error(e)
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.email).set(user).await()
        // Προσθήκη καταγραφής
        println("User saved to Firestore with email: ${user.email}")
    }


    suspend fun login(email: String, password: String): MyResult<Boolean> =
    try {
        auth.signInWithEmailAndPassword(email, password).await()
        MyResult.Success(true)
    }catch (e: Exception){
        MyResult.Error(e)
    }

    suspend fun getUserDetails(email: String): MyResult<User> {
        return try {
            val querySnapshot = firestore.collection("users").whereEqualTo("email", email).get().await()
            if (querySnapshot.isEmpty) {
                MyResult.Error(Exception("User not found"))
            } else {
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                val user = documentSnapshot?.toObject(User::class.java)
                if (user != null) {
                    MyResult.Success(user)
                } else {
                    MyResult.Error(Exception("User object is null"))
                }
            }
        } catch (e: Exception) {
            MyResult.Error(e)
        }
    }


}