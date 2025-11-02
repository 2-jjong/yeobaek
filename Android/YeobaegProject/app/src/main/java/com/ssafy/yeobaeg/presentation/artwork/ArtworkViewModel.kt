// ArtExploreViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.repository.RecentSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchMode(val title: String) {
    BROWSE("작품 둘러보기"),
    RECENT("검색"),
    RESULT("검색 결과")
}

enum class ArtworkCategory(val label: String) {
    ALL("전체"),
    ORIENTAL("동양화"),
    WESTERN("서양화"),
    CONTEMPORARY("현대미술");
}

data class ArtistResult(
    val name: String,
    val subtitle: String,
    val worksCount: Int,
    val avatarUrl: String
)

data class ArtExploreState(
    val mode: SearchMode = SearchMode.BROWSE,
    val query: String = "",
    val recent: List<String> = emptyList(),
    val selectedCategory: ArtworkCategory = ArtworkCategory.ALL,
    val categories: List<ArtworkCategory> = ArtworkCategory.entries,
    val artworks: List<String> = demoImages(),
    val resultArtist: List<ArtistResult> = emptyList(),
    val resultArtworks: List<String> = emptyList(),
    val maxWidthCm: Int? = null,
    val maxHeightCm: Int? = null
)

@HiltViewModel
class ArtExploreViewModel @Inject constructor(
    private val recentSearchRepository: RecentSearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtExploreState())
    val uiState: StateFlow<ArtExploreState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            recentSearchRepository.getRecentKeywords().collect { recents ->
                update { copy(recent = recents) }
            }
        }
    }

    fun selectTabSearch() = update { copy(mode = SearchMode.RECENT) }
    fun selectTabBrowse() = update { copy(mode = SearchMode.BROWSE) }
    fun setQuery(q: String) = update { copy(query = q) }

    fun setSizeFilter(maxWidth: Int?, maxHeight: Int?) = update {
        copy(maxWidthCm = maxWidth, maxHeightCm = maxHeight)
    }

    fun selectCategory(cat: ArtworkCategory) = update { copy(selectedCategory = cat) }

    fun search() = viewModelScope.launch {
        val q = _uiState.value.query.trim()
        if (q.isEmpty()) return@launch
        recentSearchRepository.add(q)

        // 데모 결과
        val artist = listOf(
            ArtistResult(
                "김아티스트",
                "현대미술과 전통의 조화를 추구하는 작가",
                12,
                "https://picsum.photos/seed/artist/120"
            )
        )
        val filtered = demoImages().filter { it.hashCode() % 2 == 0 }
        update { copy(mode = SearchMode.RESULT, resultArtist = artist, resultArtworks = filtered) }
    }

    fun clearRecent() = viewModelScope.launch { recentSearchRepository.clear() }
    fun removeRecent(item: String) = viewModelScope.launch { recentSearchRepository.remove(item) }

    private inline fun update(block: ArtExploreState.() -> ArtExploreState) {
        _uiState.value = _uiState.value.block()
    }
}

private fun demoImages(): List<String> = listOf(
    "https://picsum.photos/seed/a1/600/600",
    "https://picsum.photos/seed/a2/600/600",
    "https://picsum.photos/seed/a3/600/600",
    "https://picsum.photos/seed/a4/600/600",
    "https://picsum.photos/seed/a5/600/600",
    "https://picsum.photos/seed/a6/600/600",
    "https://picsum.photos/seed/a7/600/600",
    "https://picsum.photos/seed/a8/600/600",
    "https://picsum.photos/seed/a9/600/600"
)
