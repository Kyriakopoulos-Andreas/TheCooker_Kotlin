package com.TheCooker.Presentation.Views.Modules.NotificationModule.Views

import androidx.lifecycle.ViewModel
import com.TheCooker.DI.Module.UserDataProvider
import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val userRepository: UserRepo,
    private val userDataProvider: UserDataProvider,

): ViewModel(){


}