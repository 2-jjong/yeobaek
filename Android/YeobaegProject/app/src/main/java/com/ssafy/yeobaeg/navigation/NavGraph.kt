package com.ssafy.yeobaeg.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.presentation.home.ArtistHomeScreen
import com.ssafy.yeobaeg.presentation.home.CafeOwnerHomeScreen
import com.ssafy.yeobaeg.presentation.home.GeneralHomeScreen
import com.ssafy.yeobaeg.presentation.login.LoginScreen
import com.ssafy.yeobaeg.presentation.onboarding.OnboardingScreen
import com.ssafy.yeobaeg.presentation.signup.SignUpScreen
import com.ssafy.yeobaeg.presentation.splash.SplashScreen

@Composable
fun NavGraph() {
    val startDestination = Screen.Splash
    val navBackStack = rememberNavBackStack(startDestination)

    NavDisplay(
        backStack = navBackStack,
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Screen.Splash -> NavEntry(key) {
                    SplashScreen(
                        onNavigateToOnboarding = {
                            navBackStack.clear()
                            navBackStack.add(Screen.Onboarding)
                        }
                    )
                }

                is Screen.Onboarding -> NavEntry(key) {
                    OnboardingScreen(
                        onNavigateToLogin = {
                            navBackStack.clear()
                            navBackStack.add(Screen.Login)
                        }
                    )
                }

                is Screen.Login -> NavEntry(key) {
                    LoginScreen(
                        onSignUpClick = {
                            navBackStack.add(Screen.SignUp)
                        },
                        onLoginSuccess = { userRole ->
                            val homeScreen = when (userRole) {
                                UserRole.GENERAL -> Screen.GeneralHome
                                UserRole.ARTIST -> Screen.ArtistHome
                                UserRole.CAFE_OWNER -> Screen.CafeOwnerHome
                            }
                            navBackStack.clear()
                            navBackStack.add(homeScreen)
                        }
                    )
                }

                is Screen.SignUp -> NavEntry(key) {
                    SignUpScreen(
                        onSignUpSuccess = {
                            // 회원가입 성공 후 로그인 화면으로
                            navBackStack.removeLastOrNull()
                        },
                        onBackClick = {
                            navBackStack.removeLastOrNull()
                        }
                    )
                }

                is Screen.GeneralHome -> NavEntry(key) {
                    GeneralHomeScreen()
                }

                is Screen.ArtistHome -> NavEntry(key) {
                    ArtistHomeScreen()
                }

                is Screen.CafeOwnerHome -> NavEntry(key) {
                    CafeOwnerHomeScreen()
                }

                else -> throw IllegalArgumentException("Unknown route: $key")
            }
        }
    )
}

 