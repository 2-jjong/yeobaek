package com.ssafy.yeobaeg.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {
    fun navigateHome() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(route = BottomNavScreen.Home)
    }

    fun navigateCafe() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Cafe
        )
    }

    fun navigateArtwork() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Artwork
        )
    }

    fun navigateChat() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Chat
        )
    }

    fun navigateProfile() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(
            route = BottomNavScreen.Profile
        )
    }
}
