package com.TheCooker.SearchToolBar.RecipeRepo

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RecipeRepoModule {

    @Provides
    @Singleton
    fun provideRecipeRepo(firestore: FirebaseFirestore): RecipeRepo {
        return RecipeRepo(firestore)

    }
}