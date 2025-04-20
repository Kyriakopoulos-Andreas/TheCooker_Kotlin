package com.TheCooker.DI.Module

import com.TheCooker.dataLayer.Repositories.UserRepo
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NotificationServiceEntryPoint {

    fun userRepo(): UserRepo
    fun userDataProvider(): UserDataProvider
}
