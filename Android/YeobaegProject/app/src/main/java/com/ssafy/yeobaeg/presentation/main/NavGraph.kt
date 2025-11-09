package com.ssafy.yeobaeg.presentation.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.common.component.YeobaegSnackbarHost
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.bottomnav.MainBottomBar
import com.ssafy.yeobaeg.navigation.bottomnav.MainTab
import com.ssafy.yeobaeg.presentation.arcore.ARMeasureScreen
import com.ssafy.yeobaeg.presentation.arcore.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.artwork.ArtExploreScreen
import com.ssafy.yeobaeg.presentation.artwork.detail.ArtworkDetailScreen
import com.ssafy.yeobaeg.presentation.chat.ChatListScreen
import com.ssafy.yeobaeg.presentation.chat.ChattingScreen
import com.ssafy.yeobaeg.presentation.home.artist.ArtistHomeScreen
import com.ssafy.yeobaeg.presentation.home.GeneralHomeScreen
import com.ssafy.yeobaeg.presentation.home.artist.ArtworkRegisterScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.CafeOwnerHomeScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.editslot.EditDisplaySlotScreen
import com.ssafy.yeobaeg.presentation.home.cafeowner.register.RegisterCafeScreen
import com.ssafy.yeobaeg.presentation.login.LoginScreen
import com.ssafy.yeobaeg.presentation.onboarding.OnboardingScreen
import com.ssafy.yeobaeg.presentation.profile.BookmarkScreen
import com.ssafy.yeobaeg.presentation.profile.ProfileEditScreen
import com.ssafy.yeobaeg.presentation.profile.ReviewManagementScreen
import com.ssafy.yeobaeg.presentation.profile.artist.ArtistProfileScreen
import com.ssafy.yeobaeg.presentation.profile.cafe.CafeOwnerProfileScreen
import com.ssafy.yeobaeg.presentation.profile.general.GeneralProfileScreen
import com.ssafy.yeobaeg.presentation.signup.SignUpScreen
import com.ssafy.yeobaeg.presentation.splash.SplashScreen

val LocalUserRole = compositionLocalOf<UserRole?> { null }

@Composable
fun NavGraph(
    navBackStack: NavBackStack,
    snackbarHostState: SnackbarHostState,
    onTabSelected: (MainTab) -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            if (navBackStack.last() is BottomNavScreen)
                MainBottomBar(
                    tabs = MainTab.entries.toList(),
                    currentTab = mapTab(navBackStack.lastOrNull()),
                    onTabSelected = onTabSelected,
                )
        },
        snackbarHost = {
            YeobaegSnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        NavDisplay(
            backStack = navBackStack,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
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
                        when (LocalUserRole.current) {
                            UserRole.CAFE_OWNER -> CafeOwnerHomeScreen()
                            UserRole.ARTIST-> ArtistHomeScreen()
                            UserRole.GENERAL, null -> GeneralHomeScreen()
                        }
                    }

                    is BottomNavScreen.Artwork -> NavEntry(key) {
                        // Artwork Screen Composable
                        ArtExploreScreen()
                    }

                    is BottomNavScreen.SpaceScanPreview -> NavEntry(key) {
                        FloorPlanPreview(key.displayWalls)
                    }

                    is BottomNavScreen.Profile -> NavEntry(key) {
                        Log.d("NavGraph", "Current UserRole: ${LocalUserRole.current?.displayName}")
                        when (LocalUserRole.current) {
                            UserRole.CAFE_OWNER -> CafeOwnerProfileScreen()
                            UserRole.ARTIST-> ArtistProfileScreen(key.userId)
                            UserRole.GENERAL, null -> GeneralProfileScreen()
                        }

                    }

                    is BottomNavScreen.Chat -> NavEntry(key) {
                        ChatListScreen()
                    }

                    is FullScreen.ProfileEdit -> NavEntry(key) {
                        ProfileEditScreen(key.userInfo, key.introduction)
                    }

                    is FullScreen.SpaceScan -> NavEntry(key) {
                        ARMeasureScreen()
                    }

                    is FullScreen.RegisterCafe -> NavEntry(key) {
                        // Cafe Registration Screen Composable
                        RegisterCafeScreen()
                    }
                    is FullScreen.RegisterArtwork -> NavEntry(key) {
                        ArtworkRegisterScreen()
                    }
                    is FullScreen.ArtworkDetail -> NavEntry(key) {
                        ArtworkDetailScreen(artworkId = key.artworkId)
                    }

                    is FullScreen.BookmarkList -> NavEntry(key) {
                        BookmarkScreen()
                    }

                    is FullScreen.ReviewManagementScreen-> NavEntry(key){
                        ReviewManagementScreen()
                    }

                    is FullScreen.EditDisplaySlotScreen -> NavEntry(key) {
                        EditDisplaySlotScreen(key.slotId)
                    }
                    is FullScreen.ChattingScreen-> NavEntry(key){
                        ChattingScreen(key.chatRoomId)
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