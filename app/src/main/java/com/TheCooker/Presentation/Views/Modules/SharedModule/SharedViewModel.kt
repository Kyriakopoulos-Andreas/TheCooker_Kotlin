package com.TheCooker.Presentation.Views.Modules.SharedModule

import androidx.lifecycle.ViewModel
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel  @Inject constructor(
    private val userRepository: UserRepo,
    private val user: UserDataProvider
) : ViewModel() {

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _selectedBottomIndex = MutableStateFlow(2) // default στο "Home"
    val selectedBottomIndex: StateFlow<Int> = _selectedBottomIndex

    fun setSelectedIndex(index: Int) {
        _selectedBottomIndex.value = index
    }

    fun startListening() {
        user.userData?.let {
            userRepository.listenForNewNotifications(it) { count ->
                _unreadCount.value = count as? Int ?: 0
            }
        }
    }
    fun initUnreadCount(){

    }
}