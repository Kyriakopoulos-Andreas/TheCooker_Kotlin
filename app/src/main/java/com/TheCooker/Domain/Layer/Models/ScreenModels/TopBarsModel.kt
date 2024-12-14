package com.TheCooker.Domain.Layer.Models.ScreenModels

data class TopBarsModel(
    var menuTopBarRoute: Boolean,
    var mealTopBarRoute: Boolean,
    var drawerMenuRoute: Boolean,
    var updateBar: Boolean,
    var flagForRecomposeMealsViewWithTheUpdatedRecipe: Boolean = false
)
