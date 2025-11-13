package com.ssafy.yeobaeg.presentation.home.artist.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.Pagination
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.cafe.RecommendedCafe
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MatchingStep {
    ARTWORK_SELECTION,  // 작품 선택
    CAFE_SELECTION      // 카페 선택
}

data class ArtistHomeMatchingUiState(
    val currentStep: MatchingStep = MatchingStep.ARTWORK_SELECTION,

    // 작품 관련
    val isLoadingArtworks: Boolean = false,
    val isLoadingMoreArtworks: Boolean = false,
    val myArtworks: List<ArtworkSummary> = emptyList(),
    val artworkPagination: Pagination? = null,
    val selectedArtworkId: Long? = null,
    val selectedArtwork: ArtworkSummary? = null,

    // 카페 관련
    val isLoadingCafes: Boolean = false,
    val isLoadingMoreCafes: Boolean = false,
    val cafes: List<RecommendedCafe> = emptyList(),
    val cafePagination: Pagination? = null,
)

@HiltViewModel
class ArtistHomeMatchingViewModel @Inject constructor(
    private val artRepository: ArtRepository,
    private val cafeRepository: CafeRepository,
    private val navigator: Navigator
) : ViewModel() {

    private var _uiState = MutableStateFlow(ArtistHomeMatchingUiState())
    val uiState = _uiState.asStateFlow()

    // ========== 작품 관련 ==========
    fun loadMyArtworks() {
        _uiState.update { it.copy(isLoadingArtworks = true, isLoadingMoreArtworks = false) }
        viewModelScope.launch {
            artRepository.getMyArtworks().onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoadingArtworks = false,
                        myArtworks = data.bookmarkArtworks,
                        artworkPagination = data.pagination,
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingArtworks = false) }
            }
        }
    }

    fun loadMoreArtworks() {
        val pagination = _uiState.value.artworkPagination ?: return
        if (_uiState.value.isLoadingMoreArtworks || !pagination.hasNext) return

        _uiState.update { it.copy(isLoadingMoreArtworks = true) }

        viewModelScope.launch {
            artRepository.getMyArtworks(
                page = pagination.currentPage + 1,
                size = pagination.pageSize
            ).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoadingMoreArtworks = false,
                        myArtworks = it.myArtworks + data.bookmarkArtworks,
                        artworkPagination = data.pagination,
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingMoreArtworks = false) }
            }
        }
    }

    fun selectArtwork(artworkId: Long) {
        val artwork = _uiState.value.myArtworks.firstOrNull { it.artworkId == artworkId }
        _uiState.update { it.copy(selectedArtworkId = artworkId, selectedArtwork = artwork) }
    }

    fun confirmArtworkSelection() {
        // 작품 선택 완료 -> 카페 선택 단계로
        _uiState.update {
            it.copy(currentStep = MatchingStep.CAFE_SELECTION)
        }
        loadCafes()
    }

    // ========== 카페 관련 ==========
    fun loadCafes() {
        val artworkId = _uiState.value.selectedArtworkId ?: return
        _uiState.update { it.copy(isLoadingCafes = true, isLoadingMoreCafes = false) }
        viewModelScope.launch {
            cafeRepository.getRecommendedCafes(artworkId = artworkId).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoadingCafes = false,
                        cafes = data.cafes,
                        cafePagination = data.pagination
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingCafes = false) }
            }
        }
    }

    fun loadMoreCafes() {
        val artworkId = _uiState.value.selectedArtworkId ?: return
        val pagination = _uiState.value.cafePagination ?: return
        if (_uiState.value.isLoadingMoreCafes || !pagination.hasNext) return

        _uiState.update { it.copy(isLoadingMoreCafes = true) }

        viewModelScope.launch {
            cafeRepository.getRecommendedCafes(
                artworkId = artworkId,
                page = pagination.currentPage + 1,
                size = pagination.pageSize
            ).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoadingMoreCafes = false,
                        cafes = it.cafes + data.cafes,
                        cafePagination = data.pagination
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingMoreCafes = false) }
            }
        }
    }

    fun navigateToCafe(cafeId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.CafeDetail(cafeId))
    }

    // ========== 공통 ==========
    fun goBackStep() {
        when (_uiState.value.currentStep) {
            MatchingStep.CAFE_SELECTION -> {
                // 카페 선택 상태 초기화하고 작품 선택으로
                _uiState.update {
                    it.copy(
                        currentStep = MatchingStep.ARTWORK_SELECTION,
                        cafes = emptyList(),
                        cafePagination = null,
                        isLoadingCafes = false,
                        isLoadingMoreCafes = false
                    )
                }
            }
            MatchingStep.ARTWORK_SELECTION -> {
                navigateBack()
            }
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}