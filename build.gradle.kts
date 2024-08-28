// build.gradle (Project)

buildscript {

    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")

    }
}


// Το plugins block δεν πρέπει να είναι εδώ, μετακινείται στο module-level build.gradle

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}