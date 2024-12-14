package com.TheCooker.Domain.Layer.Models.ScreenModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TopBarMenuModel(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNEWS: Boolean,
    val badgeCount: Int? = null,

) {

    object ListView : TopBarMenuModel(
        route = "ListView",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Filled.List,
        hasNEWS = false

    )


    object ProfileView : TopBarMenuModel(
        route = "ProfileView",
        selectedIcon = Icons.Filled.Face,
        unselectedIcon = Icons.Filled.Face,
        hasNEWS = false
    )

    object HomeView : TopBarMenuModel(
        route = "HomeView",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Filled.Home,
        hasNEWS = false,

    )

    object SearchView : TopBarMenuModel(
        route = "SearchView",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Filled.Search,
        hasNEWS = false
    )

    object ChatView : TopBarMenuModel(
        route = "ChatView",
        selectedIcon = Icons.Filled.Email,
        unselectedIcon = Icons.Filled.Email,
        hasNEWS = false,
        badgeCount = 52,

    )

    companion object {

        val itemsList = listOf(
            ListView,
            ProfileView,
            HomeView,
            SearchView,
            ChatView
        )
    }
}

sealed class SearchScreens(
    val route: String
){
    object CategoryView: SearchScreens(
        route = "MealsView"
    )

    object MealDetailView: SearchScreens(
        route = "MealDetailView"
    )

    companion object{
        val mealsScreenList = listOf(
            CategoryView,
            MealDetailView
        )
    }

}