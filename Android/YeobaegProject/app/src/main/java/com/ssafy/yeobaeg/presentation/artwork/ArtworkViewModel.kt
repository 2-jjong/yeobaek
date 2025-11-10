// ArtExploreViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.RecentSearchRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.SnackBarController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtExploreViewModel @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository,
    private val artRepository: ArtRepository,
    private val navigator: Navigator,
    private val snackBarController: SnackBarController
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtExploreState())
    val uiState: StateFlow<ArtExploreState> = _uiState.asStateFlow()

    init {
        loadArtworks()

        viewModelScope.launch {
            recentSearchRepository.getRecentKeywords().collect { recents ->
                update { copy(recent = recents) }
            }
        }
    }

    private fun loadArtworks(category: ArtworkCategory = ArtworkCategory.ALL) {
        viewModelScope.launch {
            artRepository.getArtworks(
                category = when (category) {
                    ArtworkCategory.ALL -> ""
                    else -> category.label
                }
            )
                .onSuccess {
                    update { copy(artworks = it) }
                }
                .onFailure {
                    Log.d("ArtExploreViewModel init", "search error: ${it.message}")
                    snackBarController.showError(it.message ?: "오류")
                }
        }
    }

    fun selectTabSearch() = update { copy(mode = SearchMode.RECENT) }
    fun selectTabBrowse() = update { copy(mode = SearchMode.BROWSE) }
    fun setQuery(q: String) = update { copy(query = q) }

    fun setSizeFilter(maxWidth: Int?, maxHeight: Int?) {
        update {
            copy(maxWidthCm = maxWidth, maxHeightCm = maxHeight)
        }

        if (_uiState.value.mode == SearchMode.RESULT) {
            search()
        }
    }

    fun selectCategory(cat: ArtworkCategory) {
        update { copy(selectedCategory = cat) }
        loadArtworks()
    }

    fun search(page: Int = 0) = viewModelScope.launch {
        val q = _uiState.value.query.trim()
        if (q.isEmpty()) return@launch
        recentSearchRepository.add(q)

        update { copy(mode = SearchMode.RESULT) }

        val result = artRepository.searchArt(
            keyword = q,
            minWidth = _uiState.value.minWidthCm,
            maxWidth = _uiState.value.maxWidthCm,
            minHeight = _uiState.value.minWidthCm,
            maxHeight = _uiState.value.maxHeightCm,
            page = page,
            size = 20
        )

        result.onSuccess { searchResult ->
            update {
                copy(
                    resultArtists = if (page == 0) searchResult.artistSummaries else resultArtists,
                    resultArtworks = if (page == 0) searchResult.artworkSummaries
                    else resultArtworks + searchResult.artworkSummaries,
                    paginationInfo = searchResult.paginationInfo,
                    isSearchLoading = false
                )
            }
        }.onFailure { error ->
            Log.d("ArtExploreViewModel", "search error: ${error.message}")
            update { copy(isSearchLoading = false) }
            snackBarController.showError(error.message ?: "오류")
        }
    }

    fun loadSearchNextPage() {
        val currentPage = _uiState.value.paginationInfo?.currentPage ?: 0
        val hasNext = _uiState.value.paginationInfo?.hasNext ?: true

        if (!_uiState.value.isSearchLoading && hasNext) {
            search(currentPage + 1)
        }
    }

    fun clearRecent() = viewModelScope.launch { recentSearchRepository.clear() }
    fun removeRecent(item: String) = viewModelScope.launch { recentSearchRepository.remove(item) }

    fun navigateToDetail(artWorkId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ArtworkDetail(artWorkId))
    }

    private inline fun update(block: ArtExploreState.() -> ArtExploreState) {
        _uiState.update(block)
    }
}