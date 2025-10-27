package com.ssafy.yeobaeg.presentation.signup

import androidx.lifecycle.ViewModel
import com.ssafy.yeobaeg.domain.model.ArtType
import com.ssafy.yeobaeg.domain.model.Gender
import com.ssafy.yeobaeg.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val error: String? = null
)

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
    
    fun updatePasswordConfirm(passwordConfirm: String) {
        _uiState.value = _uiState.value.copy(passwordConfirm = passwordConfirm)
    }
    
    fun selectUserRole(role: UserRole) {
        val totalSteps = if (role == UserRole.ARTIST) 5 else 4
        _uiState.value = _uiState.value.copy(
            userRole = role,
            totalSteps = totalSteps
        )
    }
    
    fun updateBirthDate(birthDate: String) {
        _uiState.value = _uiState.value.copy(birthDate = birthDate)
    }
    
    fun selectGender(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender)
    }
    
    fun updateNickname(nickname: String) {
        _uiState.value = _uiState.value.copy(nickname = nickname)
    }
    
    fun selectArtType(artType: ArtType) {
        _uiState.value = _uiState.value.copy(artType = artType)
    }
    
    fun nextStep() {
        _uiState.value = _uiState.value.copy(
            currentStep = _uiState.value.currentStep + 1
        )
    }
    
    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(
                currentStep = _uiState.value.currentStep - 1
            )
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
}
