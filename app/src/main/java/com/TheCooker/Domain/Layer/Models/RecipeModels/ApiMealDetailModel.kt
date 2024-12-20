package com.TheCooker.Domain.Layer.Models.RecipeModels


data class ApiMealDetailModel(
    val idMeal: String = "",
    val strMeal: String = "",
    val strDrinkAlternative: String? = null,
    val strCategory: String="",
    val strArea: String="",
    val strInstructions: String= "",
    val strMealThumb: String = "",
    val strTags: String = "",
    val strYoutube: String = "",
    val strIngredient1: String? = null,
    val strIngredient2: String?= null,
    val strIngredient3: String?= null,
    val strIngredient4: String?= null,
    val strIngredient5: String?= null,
    val strIngredient6: String?= null,
    val strIngredient7: String?= null,
    val strIngredient8: String?= null,
    val strIngredient9: String?= null,
    val strIngredient10: String?= null,
    val strIngredient11: String?= null,
    val strIngredient12: String?= null,
    val strIngredient13: String?= null,
    val strIngredient14: String?= null,
    val strIngredient15: String?= null,
    val strIngredient16: String?= null,
    val strIngredient17: String?= null,
    val strIngredient18: String?= null,
    val strIngredient19: String?= null,
    val strIngredient20: String?= null,
    val strMeasure1: String?= null,
    val strMeasure2: String?= null,
    val strMeasure3: String?= null,
    val strMeasure4: String?= null,
    val strMeasure5: String?= null,
    val strMeasure6: String?= null,
    val strMeasure7: String?= null,
    val strMeasure8: String?= null,
    val strMeasure9: String?= null,
    val strMeasure10: String?= null,
    val strMeasure11: String?= null,
    val strMeasure12: String?= null,
    val strMeasure13: String?= null,
    val strMeasure14: String?= null,
    val strMeasure15: String?= null,
    val strMeasure16: String?= null,
    val strMeasure17: String?= null,
    val strMeasure18: String?= null,
    val strMeasure19: String?= null,
    val strMeasure20: String?= null,
    val strSource: String?= null,
    val strImageSource: String?= null,
    val strCreativeCommonsConfirmed: String?= null,
    val dateModified: String?= null
) {
    fun getIngredientsWithMeasures(): List<Pair<String?, String?>> {
        return listOf(
            Pair(strIngredient1, strMeasure1), Pair(strIngredient2, strMeasure2),
            Pair(strIngredient3, strMeasure3), Pair(strIngredient4, strMeasure4),
            Pair(strIngredient5, strMeasure5), Pair(strIngredient6, strMeasure6),
            Pair(strIngredient7, strMeasure7), Pair(strIngredient8, strMeasure8),
            Pair(strIngredient9, strMeasure9), Pair(strIngredient10, strMeasure10),
            Pair(strIngredient11, strMeasure11), Pair(strIngredient12, strMeasure12),
            Pair(strIngredient13, strMeasure13), Pair(strIngredient14, strMeasure14),
            Pair(strIngredient15, strMeasure15), Pair(strIngredient16, strMeasure16),
            Pair(strIngredient17, strMeasure17), Pair(strIngredient18, strMeasure18),
            Pair(strIngredient19, strMeasure19), Pair(strIngredient20, strMeasure20)
        ).filterNot { it.first.isNullOrEmpty() }
    }




}

data class MealsDetailsResponse(val meals: List<ApiMealDetailModel>)