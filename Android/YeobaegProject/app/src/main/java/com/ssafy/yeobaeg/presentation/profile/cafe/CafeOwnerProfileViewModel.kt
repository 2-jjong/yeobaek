package com.ssafy.yeobaeg.presentation.profile.cafe


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.CafeInfo
import com.ssafy.yeobaeg.domain.model.UserInfo
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
data class CafeProfileUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo = UserInfo(),
    val cafeInfo: CafeInfo= CafeInfo(),
    val errorMessage: String? = null
)

@HiltViewModel
class CafeOwnerProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository

) : ViewModel(){
    private val _uiState = MutableStateFlow(CafeProfileUiState())
    val uiState: StateFlow<CafeProfileUiState> = _uiState.asStateFlow()

    init {
        loadCafeProfile()
    }

    private fun loadCafeProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            profileRepository.getProfile()
                .onSuccess { profile ->
                    if (profile.cafeInfo != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                userInfo = profile.userInfo,
                                cafeInfo = profile.cafeInfo
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "카페 정보가 없습니다"
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "알 수 없는 오류"
                        )
                    }
                }
        }
    }

    fun refresh() {
        loadCafeProfile()
    }
    fun navigateToEditProfile() = viewModelScope.launch {
        navigator.navigate(FullScreen.ProfileEdit)
    }
    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

}