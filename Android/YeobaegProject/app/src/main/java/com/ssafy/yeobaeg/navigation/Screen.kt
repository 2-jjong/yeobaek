package com.ssafy.yeobaeg.navigation

import androidx.navigation3.runtime.NavKey
import com.ssafy.yeobaeg.domain.model.DisplayWall
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey

sealed interface FullScreen : Screen {
    @Serializable
    data object Splash : FullScreen

    @Serializable
    data object Onboarding : FullScreen

    @Serializable
    data object Login : FullScreen

    @Serializable
    data object SignUp : FullScreen

    @Serializable
    data object  ProfileEdit: FullScreen
}

sealed interface BottomNavScreen : Screen {
    // 홈 화면들
    @Serializable
    data object Home : BottomNavScreen

    @Serializable
    data object Cafe : BottomNavScreen

    @Serializable
    data object Artwork : BottomNavScreen

    @Serializable
    data object Chat : BottomNavScreen

    @Serializable
    data object Profile : BottomNavScreen

    @Serializable
    data class SpaceScanPreview(val displayWalls: List<DisplayWall>) : BottomNavScreen
}