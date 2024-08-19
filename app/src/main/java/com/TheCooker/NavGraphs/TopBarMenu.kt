package com.TheCooker.NavGraphs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TopBarMenu(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNEWS: Boolean,
    val badgeCount: Int? = null,

) {

    object ListView : TopBarMenu(
        route = "ListView",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Filled.List,
        hasNEWS = false

    )


    object ProfileView : TopBarMenu(
        route = "ProfileView",
        selectedIcon = Icons.Filled.Face,
        unselectedIcon = Icons.Filled.Face,
        hasNEWS = false
    )

    object HomeView : TopBarMenu(
        route = "HomeView",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Filled.Home,
        hasNEWS = false,

    )

    object SearchView : TopBarMenu(
        route = "SearchView",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Filled.Search,
        hasNEWS = false
    )

    object ChatView : TopBarMenu(
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