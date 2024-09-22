package com.TheCooker.SearchToolBar.ApiService

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.TheCooker.SearchToolBar.RecipeRepo.MealItem

data class UserRecipe(
    val categoryId: String? = null,
    val recipeId: String? = null,
    val recipeName: String? = null,
    val recipeIngredients: List<String>? = null,
    val steps: List<String>? = null,
    var recipeImage: String? = null,
    val creatorId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isUserRecipe: Boolean = true
) : Parcelable, MealItem {
    override val id: String? get() = recipeId
    override val name: String? get() = recipeName
    override val image: String? get() = recipeImage
    override val isUserMeal: Boolean get() = isUserRecipe


    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readBoolean()
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryId)
        parcel.writeString(recipeId)
        parcel.writeString(recipeName)
        parcel.writeStringList(recipeIngredients)
        parcel.writeStringList(steps)
        parcel.writeString(recipeImage)
        parcel.writeString(creatorId)
        parcel.writeLong(timestamp)
        parcel.writeBoolean(isUserRecipe)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRecipe> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): UserRecipe {
            return UserRecipe(parcel)
        }

        override fun newArray(size: Int): Array<UserRecipe?> {
            return arrayOfNulls(size)
        }
    }
}

data class UserResponse(val userMeal: UserRecipe?)

