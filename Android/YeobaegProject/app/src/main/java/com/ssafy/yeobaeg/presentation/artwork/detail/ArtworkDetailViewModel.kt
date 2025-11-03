// presentation/artwork/detail/ArtworkDetailViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
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
    private val navigator: Navigator
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
            }
        }
    }
}
