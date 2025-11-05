package com.ssafy.yeobaeg.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authSessionManager: AuthSessionManager,
) : ViewModel() {

    val userRole: StateFlow<UserRole?> = authSessionManager.currentUserRole

    init {
        // 토큰 만료 이벤트 구독
        viewModelScope.launch {
            authSessionManager.authEvents.collect { event ->
                when (event) {
                    AuthEvent.TokenExpired, AuthEvent.LoggedOut -> {
                        // 토큰이 만료되면 로그인 화면으로 이동
                        navigator.navigateAndClearBackStack(FullScreen.Login)
                    }
                }
            }
        }
    }
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
            route = BottomNavScreen.Profile(null)
        )
    }
}
