package com.TheCooker.Menu

data class topBars(
    var menuTopBarRoute: Boolean,
    var mealTopBarRoute: Boolean,
    var drawerMenuRoute: Boolean,
    var updateBar: Boolean,
    var flagForRecomposeMealsViewWithTheUpdatedRecipe: Boolean = false
)
