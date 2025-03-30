package com.TheCooker.DI.Module

import android.content.Context
import com.TheCooker.Domain.Layer.UseCase.GoogleIntents.GoogleClient
import com.TheCooker.dataLayer.Repositories.UserRepo
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Ότι είναι singleton στην εφαρμογή
object GoogleClientModule {

    @Provides
    @Singleton
    fun provideGoogleClient(
        @ApplicationContext context: Context,
        client: SignInClient,
        userRepo: UserRepo,
        userDataProvider: UserDataProvider
    ): GoogleClient {
        return GoogleClient(context, client, userRepo, userDataProvider)
    }
}