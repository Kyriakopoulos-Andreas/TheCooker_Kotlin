package com.TheCooker.DI.Module

import com.TheCooker.Domain.Layer.Models.LoginModels.UserDataModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataProvider@Inject constructor() {
    var userData: UserDataModel? = null
}