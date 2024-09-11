package com.TheCooker.SearchToolBar.RecipeRepo

import android.os.Parcel
import android.os.Parcelable

data class MealsCategory(
    val strMeal: String,
    val strMealThumb: String,
    val idMeal: String
) : Parcelable, MealItem {
    override val id: String? get() = idMeal
    override val name: String? get() = strMeal
    override val image: String? get() = strMealThumb

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(strMeal)
        parcel.writeString(strMealThumb)
        parcel.writeString(idMeal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MealsCategory> {
        override fun createFromParcel(parcel: Parcel): MealsCategory {
            return MealsCategory(parcel)
        }

        override fun newArray(size: Int): Array<MealsCategory?> {
            return arrayOfNulls(size)
        }
    }
}


data class MealsCategoryResponse(val meals: List<MealsCategory>)


