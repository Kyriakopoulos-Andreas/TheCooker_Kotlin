package com.TheCooker.Login.CrPassword

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepo(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signUp(
        passwordName: String,
        password: String,
        email: String
    ): MyResult<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            // Προσθήκη καταγραφής
            println("User created with email: $email")

            // Προσθήκη χρήστη στο Firestore
            val user = User(
                passwordName = passwordName,
                password = password,
                email = email
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
}