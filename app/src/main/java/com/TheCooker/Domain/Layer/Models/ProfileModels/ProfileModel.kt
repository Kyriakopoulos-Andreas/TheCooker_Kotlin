package com.TheCooker.Domain.Layer.Models.ProfileModels

sealed class ProfileModel(
    val title: String
                          ){
    data object Shares: ProfileModel(
        title = "Shares",
    )
    data object Informations: ProfileModel(
        title = "Information's",
    )

    companion object{
        val itemsList = listOf(
            Shares,
            Informations
        )
    }


}
