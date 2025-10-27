package com.ssafy.yeobaeg.domain.usecase

import com.ssafy.yeobaeg.domain.model.User
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("이메일과 비밀번호를 입력해주세요"))
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(IllegalArgumentException("올바른 이메일 형식이 아닙니다"))
        }
        
        return authRepository.login(email, password)
    }
}

