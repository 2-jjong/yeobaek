package com.ssafy.yeobaeg.presentation.profile.artist

import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.Pagination
import com.ssafy.yeobaeg.domain.model.profile.ArtistInfo
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.profile.ProfileViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistProfileUiState(
    val isLoading: Boolean = false,
    val artistInfo: ArtistInfo? = null,
    val userInfo: UserInfo = UserInfo(),
    val pagination: Pagination = Pagination(),
    val images: List<String> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ArtistProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val profileRepository: ProfileRepository

) : ProfileViewModel(navigator) {
    private val _uiState = MutableStateFlow(ArtistProfileUiState())
    val uiState: StateFlow<ArtistProfileUiState> = _uiState.asStateFlow()
    private var page = 0
    private val pageSize = 12

    fun loadArtistProfile(userId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = if (userId == null) {
                profileRepository.getProfile() // 내 프로필
            } else {
                profileRepository.getSomeoneProfile(userId) // 다른 사람 프로필
            }

            result
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userInfo = profile.userInfo,
                            artistInfo = profile.artistInfo,
                        )
                    }
                    loadNextArtworks(reset = true)
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


    /** 페이징 로드 (더미 이미지 기반) */
    fun loadNextArtworks(reset: Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value.isLoading) return@launch

            _uiState.update { it.copy(isLoading = true) }

            delay(500) // 로딩 효과 시각화용 딜레이

            val nextPage = if (reset) 0 else page + 1
            val startIndex = nextPage * pageSize

            val newImages = List(pageSize) { index ->
                "https://picsum.photos/200/200?random=${startIndex + index}"
            }

            _uiState.update {
                it.copy(
                    images = if (reset) newImages else it.images + newImages,
                    pagination = it.pagination.copy(hasNext = nextPage < 4), // 5페이지까지 존재한다고 가정
                    isLoading = false
                )
            }

            page = nextPage
        }
    }

    fun refresh() {
        page = 0
        loadNextArtworks(reset = true)

    }

}