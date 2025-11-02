package com.ssafy.yeobaeg.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.ArtType
import com.ssafy.yeobaeg.domain.model.Gender
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

data class SignUpUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 4,
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val userRole: UserRole? = null,
    val birthDate: String = "",
    val gender: Gender? = null,
    val nickname: String = "",
    val artType: ArtType? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }
    
    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }
    
    fun updatePasswordConfirm(passwordConfirm: String) {
        _uiState.update { it.copy(passwordConfirm = passwordConfirm) }
    }
    
    fun selectUserRole(role: UserRole) {
        val totalSteps = if (role == UserRole.ARTIST) 5 else 4
        _uiState.update { 
            it.copy(
                userRole = role,
                totalSteps = totalSteps
            )
        }
    }
    
    fun updateBirthDate(birthDate: String) {
        _uiState.update { it.copy(birthDate = birthDate) }
    }
    
    fun selectGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }
    
    fun updateNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }
    
    fun selectArtType(artType: ArtType) {
        _uiState.update { it.copy(artType = artType) }
    }

    fun onBack() {
        viewModelScope.launch {
            navigator.navigateBack()
        }
    }

    fun nextStep() {
        viewModelScope.launch {
            if(_uiState.value.currentStep == 4) {
                if(_uiState.value.userRole != UserRole.ARTIST) {
                    // 회원가입 처리
                    performSignUp()
                    return@launch
                }
            } else if(_uiState.value.currentStep == 5) {
                // 회원가입 처리
                performSignUp()
                return@launch
            }
            
            _uiState.update { 
                it.copy(currentStep = it.currentStep + 1)
            }
        }
    }
    
    private suspend fun performSignUp() {
        val state = _uiState.value
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        val userType = when (state.userRole) {
            UserRole.GENERAL -> "GENERAL"
            UserRole.CAFE_OWNER -> "CAFE_OWNER"
            UserRole.ARTIST -> "ARTIST"
            null -> {
                _uiState.update { it.copy(isLoading = false, error = "사용자 유형을 선택해주세요") }
                return
            }
        }
        
        val gender = when (state.gender) {
            Gender.MALE -> "MALE"
            Gender.FEMALE -> "FEMALE"
            null -> {
                _uiState.update { it.copy(isLoading = false, error = "성별을 선택해주세요") }
                return
            }
        }
        
        val result = authRepository.signUp(
            userType = userType,
            email = state.email,
            password = state.password,
            passwordConfirm = state.passwordConfirm,
            nickname = state.nickname,
            birthDate = state.birthDate,
            gender = gender
        )
        
        result.fold(
            onSuccess = { message ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        successMessage = message
                    )
                }
                // 홈 화면으로 이동
                navigator.navigateAndClearBackStack(FullScreen.Login)
            },
            onFailure = { exception ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "회원가입에 실패했습니다"
                    )
                }
            }
        )
    }
    
    fun previousStep() {
        _uiState.update { state ->
            if (state.currentStep > 1) {
                state.copy(currentStep = state.currentStep - 1)
            } else {
                state
            }
        }
    }
    
    fun canProceedFromEmailPasswordStep(): Boolean {
        val state = _uiState.value
        return state.email.isNotBlank() && 
               state.password.isNotBlank() && 
               state.password == state.passwordConfirm &&
               state.password.length >= 6
    }
    
    fun canProceedFromRoleSelectionStep(): Boolean {
        return _uiState.value.userRole != null
    }
    
    fun canProceedFromGenderAndAgeStep(): Boolean {
        val state = _uiState.value
        return state.birthDate.isNotBlank() && state.gender != null
    }
    
    fun canProceedFromNicknameStep(): Boolean {
        return _uiState.value.nickname.isNotBlank()
    }
    fun canProceedFromArtTypeStep(): Boolean {
        return _uiState.value.artType != null
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
