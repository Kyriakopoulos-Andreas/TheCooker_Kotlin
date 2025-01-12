package com.TheCooker.Domain.Layer.Models.ScreenModels

import androidx.annotation.DrawableRes

import com.TheCooker.R


sealed class DrawerScreensModel(
    val route: String,
    val hasNews: Boolean? = null,
    val badgeCount: Int? = null,
    val title: String,
    @DrawableRes val icon: Int? = null

) {

    data object Profile : DrawerScreensModel(
        route = "Profile",
        hasNews = false,
        title = "Profile",
        icon = R.drawable.baseline_face_retouching_natural_24
    )

    data object Calendar : DrawerScreensModel(
        route = "Calendar",
        hasNews = false,
        title = "Calendar",
        icon = R.drawable.baseline_edit_calendar_24


    )

    data object Settings : DrawerScreensModel(
        route = "Options",
        hasNews = false,
        title = "Options",
        icon = R.drawable.baseline_settings_24

    )


    data object Information : DrawerScreensModel(
        route = "Information",
        hasNews = false,
        title = "Information",
        icon = R.drawable.information

    )

    data object Help : DrawerScreensModel(
        route = "Help",
        title = "Help",
        icon = R.drawable.baseline_help_24

    )

    data object Logout : DrawerScreensModel(
        route = "LogOut",
        title = "Log out",
        icon = R.drawable.baseline_logout_24

    )

    companion object{
        val drawerScreensList = listOf(
            Profile,
            Calendar,
            Settings,
            Information,
            Help,
            Logout
        )
    }
}

