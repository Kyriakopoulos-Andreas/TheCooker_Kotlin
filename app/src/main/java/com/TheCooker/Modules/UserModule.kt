package com.TheCooker.Modules

import com.TheCooker.Login.SignIn.UserData
import com.TheCooker.Login.SignIn.UserDataProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideUserDataProvider(): UserDataProvider {
        return UserDataProvider()
    }


}