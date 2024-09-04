package com.TheCooker.Login.Authentication.GoogleAuth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.TheCooker.Login.CrPassword.UserRepo
import com.TheCooker.Login.SignIn.LoginResults
import com.TheCooker.Login.SignIn.UserData
import com.TheCooker.Login.SignIn.UserDataProvider

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

    suspend fun signInWithIntent(intent: Intent): LoginResults<UserData> {
        val credential = client.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            val userExists = userRepo.checkIfUserExistsInFirestore(user?.email ?: "")

            println("User exists: $userExists")
            println("Email: ${user?.email}")
            println("Name: ${user?.displayName}")

            // Login με google. Την πρωτη φορα που θα γραφτει γραφεται και στο firestore
            if (!userExists) {
                val userData = UserData(
                    userName = user?.displayName ?: "",
                    email = user?.email ?: "",
                    uid = user?.uid ?: "",
                    googleUserId = user?.uid,
                    profilerPictureUrl = user?.photoUrl?.toString()
                )
                userRepo.saveUserToFirestore(userData)
            }

            val userData = UserData(
                uid = user?.uid ?: "",
                googleUserId = user?.uid,
                userName = user?.displayName,
                profilerPictureUrl = user?.photoUrl?.toString()
            )
            userDataProvider.userData = userData

            LoginResults.Success(userData)


        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            LoginResults.Error(e)


        }
    }

    suspend fun signOut() {
        try {
            client.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            googleUserId = uid,
            userName = displayName,
            profilerPictureUrl = photoUrl?.toString(),
        )
    }

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
