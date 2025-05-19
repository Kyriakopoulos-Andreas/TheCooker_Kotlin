package com.TheCooker.Presentation.Views.Modules.TopBarViews

data class TopBarState(
    var currentRoute: TopBarRoute = TopBarRoute.Home,
    var shouldRecompose: Boolean = false,
    var previousRoute: TopBarRoute = TopBarRoute.Home,
    var shouldRecomposeMeals: Boolean = false
)
