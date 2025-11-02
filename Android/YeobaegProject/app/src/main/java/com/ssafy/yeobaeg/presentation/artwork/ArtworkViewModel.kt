// ArtExploreViewModel.kt
package com.ssafy.yeobaeg.presentation.artwork

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchMode { BROWSE, RECENT, RESULT }

data class ArtistResult(
    val name: String,
    val subtitle: String,
    val worksCount: Int,
    val avatarUrl: String
)

data class ArtExploreState(
    val mode: SearchMode = SearchMode.BROWSE,
    val query: String = "",
    val recent: List<String> = listOf("현대 미술"),
    val selectedCategory: String = "전체",
    val categories: List<String> = listOf("전체", "동양화", "서양화", "현대미술"),
    val artworks: List<String> = demoImages(),
    val resultArtist: List<ArtistResult> = emptyList(),
    val resultArtworks: List<String> = emptyList(),
    val maxWidthCm: Int? = null,
    val maxHeightCm: Int? = null
)

@HiltViewModel
class ArtExploreViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(ArtExploreState())
    val uiState: StateFlow<ArtExploreState> = _uiState.asStateFlow()

    fun selectTabSearch() = update { copy(mode = SearchMode.RECENT) }

    fun selectTabBrowse() = update { copy(mode = SearchMode.BROWSE) }

    fun setQuery(q: String) = update { copy(query = q) }

    fun setSizeFilter(maxWidth: Int?, maxHeight: Int?) = update {
        copy(maxWidthCm = maxWidth, maxHeightCm = maxHeight)
    }

    fun search() = viewModelScope.launch {
        val q = _uiState.value.query.trim()
        if (q.isEmpty()) return@launch
        val cleaned = (listOf(q) + _uiState.value.recent).distinct().take(10)
        val artist = listOf(ArtistResult("김아티스트", "현대미술과 전통의 조화를 추구하는 작가", 12, "https://picsum.photos/seed/artist/120"))
        val filtered = demoImages().filter { it.hashCode() % 2 == 0 } // 데모
        update { copy(mode = SearchMode.RESULT, recent = cleaned, resultArtist = artist, resultArtworks = filtered) }
    }

    fun clearRecent() = update { copy(recent = emptyList()) }
    fun removeRecent(item: String) = update { copy(recent = recent.filterNot { it == item }) }

    fun selectCategory(cat: String) = update { copy(selectedCategory = cat) }

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
