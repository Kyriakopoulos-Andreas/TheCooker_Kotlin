package com.TheCooker.Presentation.Views.Modules.FriendReuestsView

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.TheCooker.Common.Layer.Resources.uploadDownloadResource
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
    private val userRepository: UserRepo,
    private val userDataProvider: UserDataProvider
) : ViewModel() {

    private val _friendSuggestions = MutableStateFlow<uploadDownloadResource<List<UserDataModel>>?>(null)
    val friendSuggestions: StateFlow<uploadDownloadResource<List<UserDataModel>>?> = _friendSuggestions.asStateFlow()

    private val _friendRequests = MutableStateFlow<uploadDownloadResource<List<UserDataModel>>?>(null)
    val friendRequests: StateFlow<uploadDownloadResource<List<UserDataModel>>?> = _friendRequests.asStateFlow()

    private val _friendRequestSentState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val friendRequestSentState: StateFlow<Map<String, Boolean>> = _friendRequestSentState


    fun removeSuggestion(email: String) {
        val currentData = _friendSuggestions.value
        if (currentData is uploadDownloadResource.Success) {
            val updatedList = currentData.data.filterNot { it.email == email }
            _friendSuggestions.value = uploadDownloadResource.Success(updatedList)
        } else {

            Log.d("Error", "Failed to remove suggestion: ${currentData}")
        }
    }

    fun fetchFriendSuggestions() {
        viewModelScope.launch {
            try {
                val result = userRepository.fetchFriendSuggests(userDataProvider)
                _friendSuggestions.value = uploadDownloadResource.Success(result)
            } catch (e: Exception) {
                _friendSuggestions.value = uploadDownloadResource.Error(e)
                Log.d("FriendRequestViewModel", "Error fetching friend suggestions on ViewModel: ${e.message}")
            }
        }
    }

    fun fetchFriendRequests(){
        viewModelScope.launch {
            try {
                val result = userRepository.fetchFriendRequests(userDataProvider)
                _friendRequests.value = uploadDownloadResource.Success(result)
            }catch(e: Exception){
                _friendRequests.value = uploadDownloadResource.Error(e)
                Log.d("FriendRequestViewModel", "Error fetching friend requests on ViewModel: ${e.message}")
            }
        }
    }


    suspend fun removeFriendRequest(receiver: UserDataModel) {
        val updatedMap = _friendRequestSentState.value.toMutableMap()
        viewModelScope.launch {
            try{
                val result = userDataProvider.userData?.let { userRepository.removeFriendRequest(receiver, it) }
                if (result == true) {
                    updatedMap[receiver.email.toString()] = false
                }else{
                    updatedMap[receiver.email.toString()] = true
                }
                _friendRequestSentState.emit(updatedMap)
            }catch (e: Exception){
                updatedMap[receiver.email.toString()]
               Log.d("FriendRequestViewModel", "Error removing friend request on ViewModel: ${e.message}")
            }

        }

    }

    suspend fun sendFriendRequest(receiver: UserDataModel) {
        viewModelScope.launch {
            Log.d("FriendRequestViewModel", "Sending friend request to: ${receiver.email}")
            val updatedMap = _friendRequestSentState.value.toMutableMap()

            try {
                Log.d("userDataProvider", "userDataProvider: ${userDataProvider.userData}")
                val result =
                    userDataProvider.userData?.let { userRepository.sendFriendRequest(it, receiver) }
                Log.d("FriendRequestViewModel", "Friend request sent successfully: $result")
                if (result == true) {
                    updatedMap[receiver.email.toString()] = true
                }else{
                    updatedMap[receiver.email.toString()] = false
                }
                _friendRequestSentState.emit(updatedMap)
            }catch (e: Exception){
                updatedMap[receiver.email.toString()]
                Log.d("FriendRequestViewModel", "Error sending friend request on ViewModel: ${e.message}")
            }
        }

    }
}
