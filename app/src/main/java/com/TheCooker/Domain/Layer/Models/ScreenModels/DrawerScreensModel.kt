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

    object calendar : DrawerScreensModel(
        route = "Calendar",
        hasNews = false,
        title = "Calendar",
        icon = R.drawable.baseline_edit_calendar_24


    )

    object settings : DrawerScreensModel(
        route = "Options",
        hasNews = false,
        title = "Options",
        icon = R.drawable.baseline_settings_24

    )


    object Information : DrawerScreensModel(
        route = "Information",
        hasNews = false,
        title = "Information",
        icon = R.drawable.information

    )

    object Help : DrawerScreensModel(
        route = "Help",
        title = "Help",
        icon = R.drawable.baseline_help_24

    )

    object logout : DrawerScreensModel(
        route = "LogOut",
        title = "Log out",
        icon = R.drawable.baseline_logout_24

    )

    companion object{
        val drawerScreensList = listOf(
            calendar,
            settings,
            Information,
            Help,
            logout
        )
    }
}

