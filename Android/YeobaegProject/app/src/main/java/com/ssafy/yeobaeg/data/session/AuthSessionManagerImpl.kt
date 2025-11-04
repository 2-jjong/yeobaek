package com.ssafy.yeobaeg.data.session

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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManagerImpl @Inject constructor(
    private val tokenDataSource: TokenDataSource
) : AuthSessionManager {
    
    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    override val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()
    
    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    override val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()
    
    override suspend fun isLoggedIn(): Boolean {
        val token = tokenDataSource.getAccessToken()
        return token != null
    }
    
    override suspend fun getCurrentUserRole(): UserRole? {
        return _currentUserRole.value
    }
    
    override suspend fun updateUserRole(role: UserRole?) {
        _currentUserRole.value = role
    }
    
    override suspend fun clearUserState() {
        _currentUserRole.value = null
    }
    
    override suspend fun emitTokenExpired() {
        _authEvents.emit(AuthEvent.TokenExpired)
    }
}
