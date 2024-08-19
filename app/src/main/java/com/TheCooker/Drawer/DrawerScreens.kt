package com.TheCooker.Drawer

import androidx.annotation.DrawableRes

import com.TheCooker.R


sealed class DrawerScreens(
    val route: String,
    val hasNews: Boolean? = null,
    val badgeCount: Int? = null,
    val title: String,
    @DrawableRes val icon: Int? = null

) {

    object calendar : DrawerScreens(
        route = "Calendar",
        hasNews = false,
        title = "Calendar",
        icon = R.drawable.baseline_edit_calendar_24


    )

    object settings : DrawerScreens(
        route = "Options",
        hasNews = false,
        title = "options",
        icon = R.drawable.baseline_settings_24

    )


    object Information : DrawerScreens(
        route = "Information",
        hasNews = false,
        title = "information",
        icon = R.drawable.information

    )

    object Help : DrawerScreens(
        route = "log out",
        title = "Help",
        icon = R.drawable.baseline_help_24

    )

    object logout : DrawerScreens(
        route = "LogOut",
        title = "log out",
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

