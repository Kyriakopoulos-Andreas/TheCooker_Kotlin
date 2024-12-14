package com.TheCooker.Domain.Layer.Models.RecipeModels

import android.os.Parcel
import android.os.Parcelable

data class CategoryModel (
    val idCategory: String? = null,
    val strCategory: String? = null,
    val strCategoryThumb: String? = null,
    val strCategoryDescription: String? = null
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(idCategory)
        parcel.writeString(strCategory)
        parcel.writeString(strCategoryThumb)
        parcel.writeString(strCategoryDescription)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryModel> {
        override fun createFromParcel(parcel: Parcel): CategoryModel {
            return CategoryModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoryModel?> {
            return arrayOfNulls(size)
        }
    }
}

data class CategoriesResponse(val categories: List<CategoryModel>)


//      "idCategory": "1",
//      "strCategory": "Beef",
//      "strCategoryThumb": "https://www.themealdb.com/images/category/beef.png",
//      "strCategoryDescription":