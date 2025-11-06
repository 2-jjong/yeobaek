package com.ssafy.yeobaeg.data.session

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.local.TokenDataSource
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManagerImpl @Inject constructor(
    private val tokenDataSource: TokenDataSource
) : AuthSessionManager {
    
    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    override val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()
    
    private val _authEvents = MutableSharedFlow<AuthEvent>()
    override val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()
    
    override suspend fun isLoggedIn(): Boolean {
        val token = tokenDataSource.getAccessToken()
        return token != null
    }
    
    override suspend fun updateUserRole(role: UserRole?) {
        _currentUserRole.update {
            role
        }
        Log.d("AuthSessionManager", "User role updated to: $role")
    }
    
    override suspend fun clearUserState() {
        _currentUserRole.value = null
    }
    
    override suspend fun logout() {
        // 유저 롤 초기화
        _currentUserRole.value = null
        // 토큰 삭제
        tokenDataSource.clearTokens()
        _authEvents.emit(AuthEvent.LoggedOut)
        Log.d("AuthSessionManager", "User logged out - role and tokens cleared")
    }
    
    override suspend fun emitTokenExpired() {
        _authEvents.emit(AuthEvent.TokenExpired)
    }

    override suspend fun emitLoginSuccess() {
        _authEvents.emit(AuthEvent.LoginSuccess)
    }

    override suspend fun emitSignUpSuccess() {
        _authEvents.emit(AuthEvent.SignUpSuccess)
    }
}
