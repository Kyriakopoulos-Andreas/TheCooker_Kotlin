package com.TheCooker.Modules

import android.content.Context
import com.TheCooker.Login.SignIn.UserDataProvider
import com.TheCooker.SearchToolBar.RecipeRepo.RecipeRepo
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
                          @ApplicationContext context: Context): RecipeRepo {
        return RecipeRepo(firestore, storage, userDataProvider, context)
    }
}