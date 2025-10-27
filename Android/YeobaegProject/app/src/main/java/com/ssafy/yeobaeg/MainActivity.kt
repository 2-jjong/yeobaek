package com.ssafy.yeobaeg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation3.runtime.rememberNavBackStack
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.presentation.main.MainViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ssafy.yeobaeg.navigation.NavGraph
import com.ssafy.yeobaeg.presentation.main.LaunchedNavigator
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
                val startDestination = FullScreen.Splash
                val navBackStack = rememberNavBackStack(startDestination)

                LaunchedNavigator(navBackStack)

                NavGraph(
                    navBackStack = navBackStack,
                    onTabSelected = {
                        when (it.route) {
                            BottomNavScreen.Home -> viewModel.navigateHome()
                            BottomNavScreen.Cafe -> viewModel.navigateCafe()
                            BottomNavScreen.Artwork -> viewModel.navigateArtwork()
                            BottomNavScreen.Chat -> viewModel.navigateChat()
                            BottomNavScreen.Profile -> viewModel.navigateProfile()
                        }
                    },
                )
            }
        }
    }
}