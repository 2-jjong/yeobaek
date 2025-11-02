package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.AuthApiService
import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.auth.SignUpRequest
import com.ssafy.yeobaeg.domain.model.User
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val json: Json
) : AuthRepository {
    
    private val currentUserFlow = MutableStateFlow<User?>(null)
    
    override suspend fun signUp(
        userType: String,
        email: String,
        password: String,
        passwordConfirm: String,
        nickname: String,
        birthDate: String,
        gender: String
    ): Result<String> {
        return try {
            val request = SignUpRequest(
                userType = userType,
                email = email,
                password = password,
                passwordConfirm = passwordConfirm,
                nickname = nickname,
                birthDate = birthDate,
                gender = gender
            )
            
            val response = authApiService.signUp(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body.message ?: "정상 처리")
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = json.decodeFromString<BaseResponse<Unit?>>(errorBody)
                        errorResponse.message ?: "회원가입에 실패했습니다"
                    } catch (e: Exception) {
                        errorBody
                    }
                } else {
                    "회원가입에 실패했습니다"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
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

