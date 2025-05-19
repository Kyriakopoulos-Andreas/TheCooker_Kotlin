package com.TheCooker.Domain.Layer.UseCase.GoogleIntents

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.TheCooker.dataLayer.Repositories.UserRepo
import com.TheCooker.Common.Layer.Resources.LoginResults
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.DI.Module.UserDataProvider

import com.TheCooker.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext


import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject


class GoogleClient@Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: SignInClient,
    private val userRepo: UserRepo,
    private val userDataProvider: UserDataProvider
) {
    private val auth = Firebase.auth // Υποκείμενη υπηρεσία αυθεντικοποίησης

    suspend fun signIn(): IntentSender? {
        val result = try {
            client.beginSignIn(buildSignInRequest()).await()

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): LoginResults<UserDataModel> {
        val credential = client.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            val userExists = userRepo.checkIfUserExistsInFirestore(user?.email ?: "")

            var userData: UserDataModel? = null

            // Login με google. Την πρωτη φορα που θα γραφτει γραφεται και στο firestore
            if (!userExists) {
                userData = UserDataModel(
                    userName = user?.displayName ?: "",
                    email = user?.email ?: "",
                    uid = user?.uid ?: "",
                    googleUserId = user?.uid,
                    profilePictureUrl = user?.photoUrl?.toString()
                )
                userDataProvider.userData = userData
                userRepo.saveUserToFirestore(userData)
            }else {
                val userFromFirestore = userRepo.getUserDetails(user?.email ?: "")

                when (userFromFirestore) {
                    is LoginResults.Success -> {
                        if (user != null) {
                            userData = UserDataModel(
                                uid = user.uid ?: "",
                                googleUserId = user.uid,
                                userName = user.displayName,
                                profilePictureUrl = (if (userFromFirestore.data.profilePictureUrl.isNullOrEmpty()) {
                                    auth.currentUser?.photoUrl
                                } else {
                                    userFromFirestore.data.profilePictureUrl
                                }).toString(),
                                email = user.email,
                                country = userFromFirestore.data.country,
                                city =  userFromFirestore.data.city,
                                specialties = userFromFirestore.data.specialties,
                                chefLevel = userFromFirestore.data.chefLevel,
                                goldenChefHats = userFromFirestore.data.goldenChefHats,
                                backGroundPictureUrl = userFromFirestore.data.backGroundPictureUrl
                            )
                        }

                        userDataProvider.userData = userData
                    }
                    is LoginResults.Error -> {
                        Log.e("User Fetch Data From Firestore",  "Unknown error")
                    }
                }

              }
            userData?.let {
                LoginResults.Success(it)
            } ?: LoginResults.Error(Exception("User data is null"))

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            LoginResults.Error(e)

        }
    }

    suspend fun signOut() {
        try {
            val currentUser = Firebase.auth.currentUser

            // **Έλεγχος αν ο χρήστης είναι ήδη συνδεδεμένος**
            if (currentUser != null) {
                Log.d("Auth", "User is currently logged in as: ${currentUser.email}")
            } else {
                Log.e("Auth", "No user is currently logged in!")
                return // Αν δεν υπάρχει χρήστης, δεν χρειάζεται sign out
            }

            // Αν ο χρήστης έχει συνδεθεί με Google Sign-In, κάνε sign out από το Google
            client.signOut().await()

            // Αποσύνδεση από το Firebase Authentication
            Firebase.auth.signOut()

            // **Έλεγχος αν έγινε sign out**
            if (Firebase.auth.currentUser == null) {
                Log.d("Auth", "User signed out successfully")
            } else {
                Log.e("Auth", "Sign out failed! User is still logged in.")
            }
        } catch (e: Exception) {
            Log.e("Auth", "Error during sign out: ${e.message}", e)
            if (e is CancellationException) throw e // Διατήρηση coroutine cancellation
        }
    }


//    fun getSignedInUser(): UserDataModel? = auth.currentUser?.run {
//        UserDataModel(
//            googleUserId = uid,
//            userName = displayName,
//            profilerPictureUrl = photoUrl?.toString(),
//        )
//    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }
}
