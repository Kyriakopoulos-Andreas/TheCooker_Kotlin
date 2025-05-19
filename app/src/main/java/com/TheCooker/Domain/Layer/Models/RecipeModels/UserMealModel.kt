package com.TheCooker.Domain.Layer.Models.RecipeModels

import android.os.Parcel
import android.os.Parcelable
import com.TheCooker.dataLayer.dto.MealItem

data class UserMealModel(
    var strMeal: String = "",
    val strMealThumb: String = "",
    val idMeal: String = "",
    val categoryId: String? = null,
    val isUserRecipe: Boolean = false
) : Parcelable, MealItem {
    override val id: String? get() = idMeal
    override val name: String? get() = strMeal
    override val image: String? get() = strMealThumb
    override val isUserMeal: Boolean get() = isUserRecipe

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(strMeal)
        parcel.writeString(strMealThumb)
        parcel.writeString(idMeal)
        parcel.writeString(categoryId)
        parcel.writeByte(if (isUserRecipe) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserMealModel> {
        override fun createFromParcel(parcel: Parcel): UserMealModel {
            return UserMealModel(parcel)
        }

        override fun newArray(size: Int): Array<UserMealModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class MealsCategoryResponse(val meals: List<UserMealModel>)
