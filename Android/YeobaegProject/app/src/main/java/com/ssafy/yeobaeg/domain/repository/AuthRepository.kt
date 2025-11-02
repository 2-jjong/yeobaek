package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.User
import com.ssafy.yeobaeg.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface AuthRepository {
    suspend fun signUp(
        userType: String,
        email: String,
        password: String,
        passwordConfirm: String,
        nickname: String,
        birthDate: String,
        gender: String
    ): Result<String>
    
    suspend fun login(email: String, password: String): Result<UserRole>
    suspend fun logout(): Result<Unit>
    suspend fun refresh(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun isLoggedIn(): Boolean
    suspend fun getAccessToken(): String?
    
    /**
     * 인증 관련 이벤트를 구독
     * (예: 토큰 만료로 인한 자동 로그아웃)
     */
    val authEvents: SharedFlow<AuthEvent>
    
    /**
     * 토큰 만료 이벤트 발행 (내부용)
     */
    suspend fun emitTokenExpired()
}

