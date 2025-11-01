package com.ssafy.yeobaeg.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.bottomnav.MainBottomBar
import com.ssafy.yeobaeg.navigation.bottomnav.MainTab
import com.ssafy.yeobaeg.presentation.arcore.ARMeasureScreen
import com.ssafy.yeobaeg.presentation.arcore.ARMeasureViewModel
import com.ssafy.yeobaeg.presentation.arcore.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.login.LoginScreen
import com.ssafy.yeobaeg.presentation.onboarding.OnboardingScreen
import com.ssafy.yeobaeg.presentation.profile.ProfileEditScreen
import com.ssafy.yeobaeg.presentation.profile.cafe.CafeOwnerProfileScreen
import com.ssafy.yeobaeg.presentation.profile.general.GeneralProfileScreen
import com.ssafy.yeobaeg.presentation.signup.SignUpScreen
import com.ssafy.yeobaeg.presentation.splash.SplashScreen

@Composable
fun NavGraph(
    navBackStack: NavBackStack,
    onTabSelected: (MainTab) -> Unit = {},
) {
    val context = LocalContext.current
    val arMeasureViewModel: ARMeasureViewModel = hiltViewModel(context as MainActivity)

    Scaffold(
        bottomBar = {
            if(navBackStack.last() is BottomNavScreen)
                MainBottomBar(
                    tabs = MainTab.entries.toList(),
                    currentTab = mapTab(navBackStack.lastOrNull()),
                    onTabSelected = onTabSelected,
                )
        }
    ) { innerPadding ->

        NavDisplay(
            backStack = navBackStack,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is FullScreen.Splash -> NavEntry(key) {
                        SplashScreen(
                            onNavigateToOnboarding = {
                                navBackStack.clear()
                                navBackStack.add(FullScreen.Onboarding)
                            }
                        )
                    }

                    is FullScreen.Onboarding -> NavEntry(key) {
                        OnboardingScreen(
                            onNavigateToLogin = {
                                navBackStack.clear()
                                navBackStack.add(FullScreen.Login)
                            }
                        )
                    }

                    is FullScreen.Login -> NavEntry(key) {
                        LoginScreen()
                    }

                    is FullScreen.SignUp -> NavEntry(key) {
                        SignUpScreen()
                    }

                    is BottomNavScreen.Home -> NavEntry(key) {
                        ARMeasureScreen(arMeasureViewModel)
                    }

                    is BottomNavScreen.SpaceScanPreview -> NavEntry(key) {
                        FloorPlanPreview(key.displayWalls)
                    }

                    is BottomNavScreen.Profile-> NavEntry(key){
                        GeneralProfileScreen()
                    }

                    is FullScreen.ProfileEdit -> NavEntry(key) {
                        ProfileEditScreen()
                    }
                    else -> throw IllegalArgumentException("Unknown route: $key")
                }
            }
        )
    }
}

private fun mapTab(
    currentRoute: NavKey?
): MainTab = when (currentRoute) {
    is BottomNavScreen.Home -> MainTab.HOME
    is BottomNavScreen.Cafe -> MainTab.CAFE
    is BottomNavScreen.Artwork -> MainTab.ARTWORK
    is BottomNavScreen.Chat -> MainTab.CHAT
    is BottomNavScreen.Profile -> MainTab.PROFILE
    else -> MainTab.HOME
}