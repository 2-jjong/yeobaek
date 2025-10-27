package com.ssafy.yeobaeg.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object SignUp : Screen

    // 홈 화면들
    @Serializable
    data object GeneralHome : Screen

    @Serializable
    data object ArtistHome : Screen

    @Serializable
    data object CafeOwnerHome : Screen
}

