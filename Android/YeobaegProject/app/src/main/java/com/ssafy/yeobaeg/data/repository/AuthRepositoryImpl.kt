package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.domain.model.User
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    private val currentUserFlow = MutableStateFlow<User?>(null)
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            delay(1000) // 네트워크 지연 시뮬레이션
            
            // 임시 응답 - 이메일에 따라 다른 역할 부여 (테스트용)
            val role = when {
                email.contains("artist") -> UserRole.ARTIST
                email.contains("cafe") -> UserRole.CAFE_OWNER
                else -> UserRole.GENERAL
            }
            
            val user = User(
                id = "user_id_123",
                email = email,
                name = "사용자",
                role = role
            )
            
            currentUserFlow.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            currentUserFlow.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getCurrentUser(): Flow<User?> {
        return currentUserFlow
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return currentUserFlow.value != null
    }
}

