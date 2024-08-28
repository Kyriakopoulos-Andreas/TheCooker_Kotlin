package com.TheCooker.SearchToolBar.ApiService

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module // Ανάγνωση της κλάσης ως Dagger module
@InstallIn(SingletonComponent::class) // Εγκαθιστά το module στο SingletonComponent, το οποίο ζει για τη διάρκεια ζωής της εφαρμογής.Μπορω να ορισω και αλλες "ζωες"
object NetworkModule {

    @Provides // Σηματοδοτεί ότι η παρακάτω συνάρτηση παρέχει μια εξάρτηση
    @Singleton // Σημαίνει ότι η συνάρτηση επιστρέφει μία singleton εξάρτηση, δηλαδή μόνο ένα αντικείμενο θα δημιουργηθεί. Ενα Instance θα χρησιμοποιθει πχ απο δυο repos. Δεν θα φτιαχτουν δυο!!!
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder() // Δημιουργία του Retrofit builder
            .baseUrl("https://www.themealdb.com/api/json/v1/1/") // Ορίζουμε τη βασική διεύθυνση URL για τα API αιτήματα
            .addConverterFactory(GsonConverterFactory.create()) // Προσθέτουμε τον GsonConverterFactory για την μετατροπή JSON σε αντικείμενα
            .build() // Δημιουργούμε το Retrofit instance
    }

    @Provides // Σηματοδοτεί ότι η παρακάτω συνάρτηση παρέχει μια εξάρτηση
    @Singleton // Σημαίνει ότι η συνάρτηση επιστρέφει μία singleton εξάρτηση
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }// Δημιουργούμε την υπηρεσία API χρησιμοποιώντας το Retrofit instance
    }