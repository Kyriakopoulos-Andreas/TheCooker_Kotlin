package com.TheCooker.SearchToolBar.RecipeRepo

import android.os.Parcel
import android.os.Parcelable

data class UserRecipe(
    val categoryId: String? = null,
    val recipeId: String? = null,
    val recipeName: String? = null,
    val recipeIngredients: List<String>? = null,
    val steps: List<String>? = null,
    var recipeImage: String? = null,
    val creatorId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable, MealItem {
    override val id: String? get() = recipeId
    override val name: String? get() = recipeName
    override val image: String? get() = recipeImage

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(categoryId)
        parcel.writeString(recipeId)
        parcel.writeString(recipeName)
        parcel.writeStringList(recipeIngredients)
        parcel.writeStringList(steps)
        parcel.writeString(recipeImage)
        parcel.writeString(creatorId)
        parcel.writeLong(timestamp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRecipe> {
        override fun createFromParcel(parcel: Parcel): UserRecipe {
            return UserRecipe(parcel)
        }

        override fun newArray(size: Int): Array<UserRecipe?> {
            return arrayOfNulls(size)
        }
    }
}

data class UserResponse(val userMeals: List<UserRecipe>)

