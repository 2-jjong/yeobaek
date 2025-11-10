// presentation/artwork/detail/ArtworkDetailViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtworkDetailUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val artwork: ArtworkDetail? = null
)

@HiltViewModel
class ArtworkDetailViewModel @Inject constructor(
    private val artRepository: ArtRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtworkDetailUiState())
    val uiState: StateFlow<ArtworkDetailUiState> = _uiState.asStateFlow()

    private var toggleJob: Job? = null

    fun onBackClick() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun load(artworkId: Long) {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            artRepository.getArtworkDetail(artworkId)
                .onSuccess { detail ->
                    _uiState.update { it.copy(loading = false, artwork = detail) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = error.message ?: "알 수 없는 오류"
                        )
                    }
                    snackBarController.showError(error.message ?: "오류")
                }
        }
    }

    fun toggleBookmark() {
        val d = _uiState.value.artwork ?: return
        val b = d.isBookmarked

        toggleJob?.cancel()

        toggleJob = viewModelScope.launch {
            _uiState.update { it.copy(artwork = d.copy(isBookmarked = !b)) }
            artRepository.toggleBookmark(d.artworkId).onSuccess { response ->
                _uiState.update { it.copy(artwork = d.copy(isBookmarked = response)) }
            }.onFailure {
                _uiState.update { it.copy(artwork = d.copy(isBookmarked = b)) }
                snackBarController.showError(it.message ?: "오류")
            }
        }
    }
    fun onInquiryClick(artistId: Long) = viewModelScope.launch {
        chatRepository.createChatRoom(artistId)
            .onSuccess { chatRoom ->
                navigator.navigate(FullScreen.ChattingScreen(chatRoom.chatRoomId))
            }
            .onFailure { e ->
                Log.e("ArtworkDetailViewModel", "채팅방 생성 실패: ${e.message}")
                snackBarController.showError("채팅방 생성 중 오류가 발생했습니다")
            }
    }

}
