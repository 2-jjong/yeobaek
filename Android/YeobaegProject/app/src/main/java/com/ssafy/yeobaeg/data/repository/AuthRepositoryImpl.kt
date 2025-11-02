package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.local.TokenDataSource
import com.ssafy.yeobaeg.data.datasource.remote.AuthApiService
import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.auth.LoginRequest
import com.ssafy.yeobaeg.data.model.auth.SignUpRequest
import com.ssafy.yeobaeg.domain.model.AuthEvent
import com.ssafy.yeobaeg.domain.model.User
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenDataSource: TokenDataSource,
    private val json: Json
) : AuthRepository {
    
    private val currentUserFlow = MutableStateFlow<User?>(null)
    
    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    override val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()
    
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
    
    override suspend fun login(email: String, password: String): Result<UserRole> {
        return try {
            val request = LoginRequest(
                email = email,
                password = password
            )
            
            val response = authApiService.login(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                val loginResponse = body?.data
                
                if (loginResponse != null) {
                    // 토큰 저장
                    tokenDataSource.saveTokens(
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken
                    )
                    
                    // UserRole 변환
                    val role = when (loginResponse.userType) {
                        "GENERAL" -> UserRole.GENERAL
                        "CAFE_OWNER" -> UserRole.CAFE_OWNER
                        "ARTIST" -> UserRole.ARTIST
                        else -> UserRole.GENERAL
                    }
                    
                    // UserRole만 반환
                    Result.success(role)
                } else {
                    Result.failure(Exception("로그인 응답 데이터가 없습니다"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorResponse = json.decodeFromString<BaseResponse<Unit?>>(errorBody)
                        errorResponse.message ?: "로그인에 실패했습니다"
                    } catch (e: Exception) {
                        errorBody
                    }
                } else {
                    "로그인에 실패했습니다"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            // 토큰 삭제
            tokenDataSource.clearTokens()
            currentUserFlow.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refresh(): Result<Unit> {
        return try {
            val refreshToken = tokenDataSource.getRefreshToken()
            if (refreshToken == null) {
                return Result.failure(Exception("Refresh token이 없습니다"))
            }
            
            val response = authApiService.refresh("Bearer $refreshToken")
            
            if (response.isSuccessful) {
                val body = response.body()
                val tokenResponse = body?.data
                
                if (tokenResponse != null) {
                    tokenDataSource.saveTokens(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("토큰 갱신 응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("토큰 갱신에 실패했습니다"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getCurrentUser(): Flow<User?> {
        return currentUserFlow
    }
    
    override suspend fun isLoggedIn(): Boolean {
        val token = tokenDataSource.getAccessToken()
        return token != null
    }
    
    override suspend fun getAccessToken(): String? {
        return tokenDataSource.getAccessToken()
    }
    
    override suspend fun emitTokenExpired() {
        _authEvents.emit(AuthEvent.TokenExpired)
    }
}

