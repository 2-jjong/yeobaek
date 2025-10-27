package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun isLoggedIn(): Boolean
}

