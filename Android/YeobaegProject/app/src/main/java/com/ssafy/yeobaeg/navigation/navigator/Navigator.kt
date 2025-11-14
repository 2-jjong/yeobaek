package com.ssafy.yeobaeg.navigation.navigator

import androidx.navigation3.runtime.NavKey

interface Navigator {

    suspend fun navigate(
        route: NavKey,
        popUpTo: NavKey? = null
    )

    suspend fun navigateBack()

    suspend fun navigateAndClearBackStack(route: NavKey)
}
