package com.ssafy.yeobaeg.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.Screen
import com.ssafy.yeobaeg.navigation.bottomnav.MainTab
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YeobaegProjectTheme(
                darkTheme = false,
                dynamicColor = false
            ) {
                val userRole by viewModel.userRole.collectAsStateWithLifecycle()
                
                CompositionLocalProvider(LocalUserRole provides userRole) {
                val startDestination = FullScreen.Login
                val navBackStack = rememberNavBackStack(startDestination)

                LaunchedNavigator(navBackStack)

                NavGraph(
                    navBackStack = navBackStack,
                    onTabSelected = {
                        when (it) {
                            MainTab.HOME -> viewModel.navigateHome()
                            MainTab.CAFE -> viewModel.navigateCafe()
                            MainTab.ARTWORK -> viewModel.navigateArtwork()
                            MainTab.CHAT -> viewModel.navigateChat()
                            MainTab.PROFILE -> viewModel.navigateProfile()
                        }
                    },
                )
                }
            }
        }
    }
}