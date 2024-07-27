package com.TheCooker.Login.Authentication

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.widget.Toast
import com.TheCooker.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleClient(
    private val context: Context,
    private val client: SignInClient
) {
    private val auth = Firebase.auth // ησιμοποιείται για την εκτέλεση διάφορων εργασιών αυθεντικοποίησης, όπως σύνδεση και αποσύνδεση χρηστών, εγγραφή νέων χρηστών, και λήψη πληροφοριών για τον τρέχοντα χρήστη.

    suspend fun signIn(context: Context): IntentSender? {
        //Intent σε μια δραστηριότητα ή υπηρεσία που βρίσκεται σε άλλη εφαρμογή, ακόμα και αν αυτή η εφαρμογή δεν είναι επί του παρόντος εκτελούμενη. Χρησιμοποιείται συχνά για να ζητήσει τη διεπαφή χρήστη (user interface) από άλλη εφαρμογή για να ολοκληρώσει κάποια ενέργεια, όπως η σύνδεση με έναν λογαριασμό Google.

        val result = try {
            client.beginSignIn(   // ξεκινάμε ενα sign in request
                buildsSignInRequest()
            ).await() // περιμενει με το await μεχρι να ολοκληρωθει το sign in

        } catch (e: Exception) {
            e.printStackTrace() // εμφανιζει μια στοιβα κλησεων της εξαιρεσης στην console
            if (e is CancellationException) throw e // Ρίψη Εξαίρεσης: Χρησιμοποιείται για να αναγγείλει μια εξαίρεση και να σταματήσει η τρέχουσα ροή εκτέλεσης για να επιτραπεί σε άλλο τμήμα του προγράμματος να την διαχειριστεί.

            // Προσθήκη Toast για σφάλματα
            Toast.makeText(context, "Sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }

        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {   // δεχεται την απαντηση που πηραμε απο το Intent και το κανει deserialize
        val credential = client.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try{
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run{
                    UserData(
                        userId = uid,
                        userName = displayName,
                        profilerPictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null

            )

        }catch (e: Exception){
            e.printStackTrace() // εμφανιζει μια στοιβα κλησεων της εξαιρεσης στην console
            if(e is CancellationException) throw e // Ρίψη Εξαίρεσης: Χρησιμοποιείται για να αναγγείλει μια εξαίρεση και να σταματήσει η τρέχουσα ροή εκτέλεσης για να επιτραπεί σε άλλο τμήμα του προγράμματος να την διαχειριστεί.
            SignInResult(
                data = null,
                errorMessage = e.message
            )

        }

    }

    suspend fun signOut(){
        try{
            client.signOut().await() // εξασφαλίζει οτι θα κανουμε Loggin μετα το log out χωρις accouny
            auth.signOut()

        }catch (e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSingedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName,
            profilerPictureUrl = photoUrl?.toString()
        )
    }

    private fun buildsSignInRequest(): BeginSignInRequest {

        return  BeginSignInRequest.
        builder().
        setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().
            setSupported(true).
            setFilterByAuthorizedAccounts(false). // Βλεπει αν έχεις κάνει loggin ξανα με το ιδιο account και την δευτερη φορα σε προτρεπει να μπεις με αυτο
            setServerClientId(context.getString(R.string.web_client_id)).build() // Περνάω το key της εφαρμογης μου για την συνδεση για την επικοινωνια με τις υπηρεσιες google

        ).setAutoSelectEnabled(false).build() // Αν έχεις μονο ένα google account θα επιλεγετε αυτοματα αυτο
    }
}