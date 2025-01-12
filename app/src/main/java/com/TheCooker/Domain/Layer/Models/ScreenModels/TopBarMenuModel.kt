package com.TheCooker.Domain.Layer.Models.ScreenModels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.TheCooker.R

sealed class TopBarMenuModel(
    val route: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val hasNEWS: Boolean,
    val badgeCount: Int? = null,

) {

    object ListView : TopBarMenuModel(
        route = "ListView",
        selectedIcon = R.drawable.drawmenu,
        unselectedIcon = R.drawable.drawmenu,
        hasNEWS = false

    )


    object FriendRequestView : TopBarMenuModel(
        route = "FriendRequestView",
        selectedIcon = R.drawable.baseline_people_24,
        unselectedIcon = R.drawable.baseline_people_24,
        hasNEWS = false
    )

    object HomeView : TopBarMenuModel(
        route = "HomeView",
        selectedIcon = R.drawable.home1,
        unselectedIcon = R.drawable.home1,
        hasNEWS = false,

    )

    object SearchView : TopBarMenuModel(
        route = "SearchView",
        selectedIcon = R.drawable.search,
        unselectedIcon = R.drawable.search,
        hasNEWS = false
    )

    object NotificationView : TopBarMenuModel(
        route = "NotificationView",
        selectedIcon = R.drawable.notifications,
        unselectedIcon = R.drawable.notifications,
        hasNEWS = false,
        badgeCount = 3,

    )

    companion object {

        val itemsList = listOf(
            ListView,
            FriendRequestView,
            HomeView,
            SearchView,
            NotificationView
        )
        @Composable
        fun TopBarMenuModel.getSelectedIcon(): ImageVector {
            return ImageVector.vectorResource(id = this.selectedIcon)
        }

        @Composable
        fun TopBarMenuModel.getUnselectedIcon(): ImageVector {
            return ImageVector.vectorResource(id = this.unselectedIcon)
        }
    }
}

sealed class SearchScreens(
    val route: String
    , val badgeCount: Int? = null
){
    object CategoryView: SearchScreens(
        route = "MealsView",
        badgeCount = 3
    )

    object MealDetailView: SearchScreens(
        route = "MealDetailView",
        badgeCount = 3
    )

    companion object{
        val mealsScreenList = listOf(
            CategoryView,
            MealDetailView
        )
    }

}