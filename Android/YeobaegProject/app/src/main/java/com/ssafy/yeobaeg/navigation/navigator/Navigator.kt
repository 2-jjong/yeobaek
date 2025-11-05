package com.ssafy.yeobaeg.navigation.navigator

import androidx.navigation3.runtime.NavKey

interface Navigator {

    suspend fun navigate(
        route: NavKey,
        saveState: Boolean = false,
        launchSingleTop: Boolean = true,
    )

    suspend fun navigateBack()

    suspend fun navigateAndClearBackStack(route: NavKey)
}
