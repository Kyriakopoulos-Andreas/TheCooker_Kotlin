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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
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

    suspend fun fetchFriendRequests(user: UserDataProvider): List<UserDataModel> {
        try {
            // Βήμα 1: Ανάκτηση του πίνακα των αιτημάτων από το PendingFriendRequests
            val userRef = firestore.collection("PendingFriendRequests").document(user.userData?.email.toString())
            val userDoc = userRef.get().await()

            // Λήψη του πεδίου "requests" που περιέχει τα emails των χρηστών
            val requests = userDoc.get("requests") as? List<String> ?: emptyList()

            // Βήμα 2: Ανάκτηση των χρηστών από τη συλλογή "users" με τα emails
            val userDataList = mutableListOf<UserDataModel>()

            // Επιστρέφουμε τα emails για αναζήτηση στα έγγραφα της συλλογής users
            for (email in requests) {
                val userSnapshot = firestore.collection("users").document(email).get().await()

                // Ελέγχουμε αν το έγγραφο υπάρχει και αν έχει τα πεδία που θέλουμε
                if (userSnapshot.exists()) {
                    val userData = userSnapshot.toObject(UserDataModel::class.java)

                    // Αν το userData δεν είναι null, προσθέτουμε το αποτέλεσμα στη λίστα
                    userData?.let {
                        userDataList.add(it)
                    }
                }
            }

            // Επιστρέφουμε τη λίστα των χρηστών
            return userDataList

        } catch (e: Exception) {
            Log.d("Error", "Error fetching friend requests: ${e.message}")
            return emptyList()
        }
    }


   fun updateUserLocation(location: LocationData, user: UserDataModel) {
       val europeanCountries = mapOf(
           "Shqipëria" to "Albania",
           "Andorra" to "Andorra",
           "Հայաստան" to "Armenia",
           "Österreich" to "Austria",
           "Azərbaycan" to "Azerbaijan",
           "Беларусь" to "Belarus",
           "België / Belgique / Belgien" to "Belgium",
           "Bosna i Hercegovina" to "Bosnia and Herzegovina",
           "България" to "Bulgaria",
           "Hrvatska" to "Croatia",
           "Κύπρος" to "Cyprus",
           "Kıbrıs" to "Cyprus",
           "Česká republika" to "Czech Republic",
           "Danmark" to "Denmark",
           "Eesti" to "Estonia",
           "Suomi" to "Finland",
           "France" to "France",
           "საქართველო" to "Georgia",
           "Deutschland" to "Germany",
           "Ελλάδα" to "Greece",
           "Greece" to "Greece",
           "Magyarország" to "Hungary",
           "Ísland" to "Iceland",
           "Éire" to "Ireland",
           "Ireland" to "Ireland",
           "Italia" to "Italy",
           "Қазақстан" to "Kazakhstan",
           "Latvija" to "Latvia",
           "Liechtenstein" to "Liechtenstein",
           "Lietuva" to "Lithuania",
           "Lëtzebuerg" to "Luxembourg",
           "Malta" to "Malta",
           "Moldova" to "Moldova",
           "Monaco" to "Monaco",
           "Crna Gora" to "Montenegro",
           "Nederland" to "Netherlands",
           "Северна Македонија" to "North Macedonia",
           "Norge" to "Norway",
           "Polska" to "Poland",
           "Portugal" to "Portugal",
           "România" to "Romania",
           "Россия" to "Russia",
           "San Marino" to "San Marino",
           "Србија" to "Serbia",
           "Slovensko" to "Slovakia",
           "Slovenija" to "Slovenia",
           "España" to "Spain",
           "Sverige" to "Sweden",
           "Schweiz" to "Switzerland",
           "Suisse" to "Switzerland",
           "Svizzera" to "Switzerland",
           "Svizra" to "Switzerland",
           "Türkiye" to "Turkey",
           "Україна" to "Ukraine",
           "United Kingdom" to "United Kingdom",
           "UK" to "United Kingdom",
           "Vaticano" to "Vatican City"
       )
       try {
           val userRef = firestore.collection("users").document(user.email.toString())
           val standardizedCountry = europeanCountries[location.country] ?: location.country
           userRef.update("countryFromWhichUserConnected", standardizedCountry)
           userRef.update("cityFromWhichUserConnected", location.city)
           userRef.update("connectedAddress", location.address)
       } catch (e: Exception) {
           Log.d("UserRepo", "Error updating user location: ${e.message}")
       }
    }

    suspend fun acceptFriendRequest(sender: UserDataModel, receiver: UserDataModel): Boolean {
        try {
            val senderEmail = sender.email ?: run {
                Log.d("UserRepo", "Sender email is null.")
                return false
            }

            val receiverEmail = receiver.email ?: run {
                Log.d("UserRepo", "Receiver email is null.")
                return false
            }

            // Διαγραφή του αιτήματος από τα Pending και Sending collections
            val pendingRef = firestore.collection("PendingFriendRequests").document(receiverEmail)
            val sendingRef = firestore.collection("SendingFriendRequests").document(senderEmail)

            pendingRef.update("requests", FieldValue.arrayRemove(senderEmail)).await()
            sendingRef.update("requests", FieldValue.arrayRemove(receiverEmail)).await()

            // Προσθήκη στους AcceptedFriendships
            val acceptedRef = firestore.collection("AcceptedFriendships").document("${senderEmail}_$receiverEmail")
            acceptedRef.set(mapOf(
                "sender" to senderEmail,
                "receiver" to receiverEmail,
                "timestamp" to FieldValue.serverTimestamp()
            )).await()

            return true
        } catch (e: FirebaseFirestoreException) {
            Log.d("UserRepo", "Firestore error: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.d("UserRepo", "Error accepting friend request: ${e.message}")
            return false
        }
    }

    suspend fun sendFriendRequest(sender: UserDataModel, receiver: UserDataModel): Boolean {
        try {

            val senderEmail = sender.email ?: run {
                Log.d("UserRepo", "Sender email is null.")
                return false
            }

            val receiverEmail = receiver.email ?: run {
                Log.d("UserRepo", "Receiver email is null.")
                return false
            }
            Log.d("UserRepo", "Sender email On Send: $senderEmail")
            Log.d("UserRepo", "Receiver email On Send: $receiverEmail")

            // Αναφορά για το Pending και το Sending
            val pendingRef = firestore.collection("PendingFriendRequests").document(receiverEmail)
            val sendingRef = firestore.collection("SendingFriendRequests").document(senderEmail)

            // Δημιουργία ή ενημέρωση του εγγράφου στο Pending collection
            val pendingData = hashMapOf("requests" to FieldValue.arrayUnion(senderEmail))
            pendingRef.set(pendingData, SetOptions.merge()).await() // Χρησιμοποιούμε set για να δημιουργήσουμε ή να ενημερώσουμε το έγγραφο
            Log.d("UserRepo", "Pending data written: $pendingData")

            // Δημιουργία ή ενημέρωση του εγγράφου στο Sending collection
            val sendingData = hashMapOf("requests" to FieldValue.arrayUnion(receiverEmail))
            sendingRef.set(sendingData, SetOptions.merge()).await() // Χρησιμοποιούμε set για να δημιουργήσουμε ή να ενημερώσουμε το έγγραφο

            createFriendRequestNotification(sender, receiver)
            return true
        } catch (e: FirebaseFirestoreException) {
            Log.d("UserRepo", "Firestore error: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.d("UserRepo", "Error sending friend request: ${e.message}")
            return false
        }
    }





    private suspend fun createFriendRequestNotification(sender: UserDataModel, receiver: UserDataModel) {
        try {
            val senderEmail = sender.email ?: run {
                Log.d("UserRepo", "Sender email is null.")
                return
            }
            val receiverEmail = receiver.email ?: run {
                Log.d("UserRepo", "Receiver email is null.")
                return
            }


            val notificationRef = firestore
                .collection("Notifications")
                .document(receiverEmail)
                .collection("FriendRequestNotifications")
                .document(senderEmail)


            val notificationData = hashMapOf(
                "senderEmail" to senderEmail,
                "timestamp" to FieldValue.serverTimestamp(),
                "status" to "Pending",
                "viewStatus" to "Unread"
            )



            notificationRef.set(notificationData).await()

            Log.d("UserRepo", "Friend request notification created for receiver: $receiverEmail")

        } catch (e: Exception) {
            Log.d("UserRepo", "Error creating friend request notification: ${e.message}")
        }
    }

    private suspend fun deleteNotification(sender: UserDataModel, receiver: UserDataModel) {
        try {

            val senderEmail = sender.email ?: run {
                Log.d("UserRepo", "Sender email is null.")
                return
            }

            val receiverEmail = receiver.email ?: run {
                Log.d("UserRepo", "Receiver email is null.")
                return
            }

            val notificationRef = firestore.collection("Notifications").document(receiverEmail)
                .collection("FriendRequestNotifications").document(senderEmail)

            notificationRef.delete().await()
        }catch(e: Exception){
            Log.d("UserRepo", "Error deleting friend request notification: ${e.message}")
        }

    }


    suspend fun removeFriendRequest(receiver: UserDataModel, sender: UserDataModel): Boolean {
        try {
            val senderEmail = sender.email ?: run {
                Log.d("UserRepo", "Sender email is null.")
                return false
            }

            val receiverEmail = receiver.email ?: run {
                Log.d("UserRepo", "Receiver email is null.")
                return false
            }

            Log.d("UserRepo On Remove", "Sender email: $senderEmail")
            Log.d("UserRepo On Remove", "Receiver email: $receiverEmail")

            // Αναφορά για το Pending και το Sending collection
            val pendingRef = firestore.collection("PendingFriendRequests").document(receiverEmail)
            val sendingRef = firestore.collection("SendingFriendRequests").document(senderEmail)

            // Διαγραφή του εγγράφου από το Pending collection
            pendingRef.update("requests", FieldValue.arrayRemove(senderEmail)).await()
            Log.d("UserRepo", "Deleted Pending request document for receiverEmail: $receiverEmail")

            // Αφαίρεση του receiverEmail από το array των requests στο Sending collection
            Log.d("UserRepo", "Removed receiverEmail: {$receiverEmail} from Sending requests")
            sendingRef.update("requests", FieldValue.arrayRemove(receiverEmail)).await()
            Log.d("UserRepo", "Removed receiverEmail from Sending requests")
            deleteNotification(sender, receiver)

            return true
        } catch (e: FirebaseFirestoreException) {
            Log.d("UserRepo", "Firestore error: ${e.message}")
            return false
        } catch (e: Exception) {
            Log.d("UserRepo", "Error removing friend request: ${e.message}")
            return false
        }
    }





    suspend fun fetchFriendSuggests(user: UserDataProvider): List<UserDataModel> {
        val suggestions = mutableListOf<UserDataModel>()
        val addedUserEmails = mutableSetOf<String>() // Set για αποφυγή διπλότυπων

        try {
            val ref = firestore.collection("users").document(user.userData?.email.toString())
            val userDoc = ref.get().await()
            val userCity = userDoc.getString("cityFromWhichUserConnected")
            val userCountry = userDoc.getString("countryFromWhichUserConnected")

            // Φέρνουμε χρήστες από την ίδια πόλη
            val cityQuery = firestore.collection("users")
                .whereEqualTo("cityFromWhichUserConnected", userCity)
                .limit(40)
                .get()
                .await()

            val alreadySendingExistsRequestsRef = firestore.collection("SendingFriendRequests").document(user.userData?.email.toString())
            val alreadySendingExistsRequestsDoc = alreadySendingExistsRequestsRef.get().await()
            val alreadySendingExistsRequests = alreadySendingExistsRequestsDoc.get("requests") as? MutableList<String> ?: emptyList()


            val friendRequests = fetchFriendRequests(user)




            cityQuery.toObjects(UserDataModel::class.java).forEach { user ->
                if (addedUserEmails.add(user.email.toString())) {
                    suggestions.add(user)
                }
            }

            // Αν δεν έχουμε 40 προτάσεις, φέρνουμε από την ίδια χώρα
            if (suggestions.size < 40) {
                val countryQuery = firestore.collection("users")
                    .whereEqualTo("countryFromWhichUserConnected", userCountry)
                    .limit((40 - suggestions.size).toLong())
                    .get()
                    .await()

                countryQuery.toObjects(UserDataModel::class.java).forEach { user ->
                    if (addedUserEmails.add(user.email.toString())) {
                        suggestions.add(user)
                    }
                }
            }





            // Αν ακόμα δεν έχουμε 40 προτάσεις, φέρνουμε τυχαία άτομα
            if (suggestions.size < 40) {
                val randomQuery = firestore.collection("users")
                    .limit((40 - suggestions.size).toLong())
                    .get()
                    .await()

                randomQuery.toObjects(UserDataModel::class.java).forEach { user ->
                    if (addedUserEmails.add(user.email.toString())) {
                        suggestions.add(user)
                    }
                }
            }

            suggestions.removeIf { it.email in alreadySendingExistsRequests }
            suggestions.removeAll(friendRequests)



            return suggestions.apply { removeIf { it.email == user.userData?.email } }

        } catch (e: Exception) {
            Log.d("UserRepo", "Error fetching friend requests: ${e.message}")
        }

        return emptyList()
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