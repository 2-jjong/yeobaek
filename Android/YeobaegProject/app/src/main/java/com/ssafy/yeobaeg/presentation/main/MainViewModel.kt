package com.ssafy.yeobaeg.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authSessionManager: AuthSessionManager,
) : ViewModel() {

    val userRole: StateFlow<UserRole?> = authSessionManager.currentUserRole

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // 인증 이벤트 구독
        viewModelScope.launch {
            authSessionManager.authEvents.collect { event ->
                when (event) {
                    AuthEvent.TokenExpired, AuthEvent.LoggedOut -> {
                        // 토큰이 만료되면 로그인 화면으로 이동
                        navigator.navigateAndClearBackStack(FullScreen.Login)
                        _snackbarMessage.update { "로그아웃 되었습니다." }
                    }
                    AuthEvent.LoginSuccess -> {
                        // 로그인 성공 스낵바 메시지 표시
                        navigator.navigateAndClearBackStack(route = BottomNavScreen.Home)
                        _snackbarMessage.update { "로그인 성공!" }
                    }
                    AuthEvent.SignUpSuccess -> {
                        // 회원가입 성공 스낵바 메시지 표시
                        navigator.navigateAndClearBackStack(FullScreen.Login)
                        _snackbarMessage.update { "회원가입에 성공했습니다!" }
                    }
                }
            }
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.update { null }
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
