package com.karthik.nimmaguru.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes {
    @Serializable object Splash : Routes
    @Serializable object Login : Routes
    @Serializable object Register : Routes
    @Serializable object Home : Routes
    @Serializable object Search : Routes
    @Serializable object Sessions : Routes
    @Serializable object Profile : Routes
    @Serializable object EditProfile : Routes
    @Serializable object Wall : Routes
    @Serializable object CreateSession : Routes

    @Serializable
    data class GuruProfile(val guruId: String) : Routes

    @Serializable
    data class Appreciation(
        val guruId: String,
        val guruName: String = "Guru"
    ) : Routes
}