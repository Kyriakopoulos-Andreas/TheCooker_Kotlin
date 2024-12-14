package com.TheCooker.DI.Module

import android.content.Context
import com.TheCooker.dataLayer.Api.ApiService
import com.TheCooker.Domain.Layer.Repositories.RecipeRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecipeRepoModule {



    @Provides
    @Singleton
    fun provideRecipeRepo(firestore: FirebaseFirestore,
                          storage: FirebaseStorage,
                          userDataProvider: UserDataProvider,
                          @ApplicationContext context: Context,
                          apiService: ApiService
    ): RecipeRepo {
        return RecipeRepo(firestore, storage, userDataProvider, apiService, context)
    }
}