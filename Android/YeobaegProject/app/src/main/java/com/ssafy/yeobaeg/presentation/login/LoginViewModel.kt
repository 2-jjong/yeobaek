package com.ssafy.yeobaeg.presentation.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.AuthRepository
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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val navigator: Navigator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }
    
    fun login() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        
        viewModelScope.launch {
            // 입력 검증
            if (email.isBlank() || password.isBlank()) {
                _uiState.update {
                    it.copy(error = "이메일과 비밀번호를 입력해주세요")
                }
                return@launch
            }
            
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _uiState.update {
                    it.copy(error = "올바른 이메일 형식이 아닙니다")
                }
                return@launch
            }
            
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }
            
            authRepository.login(email, password)
                .onSuccess { userRole ->
                    _uiState.update {
                        it.copy(isLoading = false, error = null)
                    }
                    
                    // UserRole에 따라 다른 홈 화면으로 이동
                    val homeScreen = when (userRole) {
                        UserRole.GENERAL -> BottomNavScreen.Home
                        UserRole.ARTIST -> BottomNavScreen.Home  // 작가는 Home
                        UserRole.CAFE_OWNER -> BottomNavScreen.Home  // 카페 사장도 Home
                    }
                    
                    navigator.navigateAndClearBackStack(homeScreen)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun navigateSignUp() = viewModelScope.launch {
        navigator.navigate(FullScreen.SignUp)
    }
    
    fun clearError() {
        _uiState.update {
            it.copy(error = null)
        }
    }
}

