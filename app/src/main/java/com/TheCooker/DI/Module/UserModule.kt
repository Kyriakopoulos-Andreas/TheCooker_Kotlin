package com.TheCooker.DI.Module

import com.TheCooker.dataLayer.Repositories.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    @Provides
    @Singleton
    fun provideUserRepo(
        auth: FirebaseAuth,
        userDataProvider: UserDataProvider,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): UserRepo {
        return UserRepo(auth, userDataProvider, firestore, storage)
    }


}