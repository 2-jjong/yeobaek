package com.ssafy.yeobaeg.presentation.artwork

import com.ssafy.yeobaeg.domain.model.PaginationInfo
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.art.ArtworkSummary


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

data class ArtExploreState(
    val mode: SearchMode = SearchMode.BROWSE,
    val query: String = "",
    val recent: List<String> = emptyList(),
    val selectedCategory: ArtworkCategory = ArtworkCategory.ALL,
    val categories: List<ArtworkCategory> = ArtworkCategory.entries,
    val artworks: List<ArtworkSummary> = emptyList(),
    val resultArtists: List<ArtistSummary> = emptyList(),
    val resultArtworks: List<ArtworkSummary> = emptyList(),
    val paginationInfo: PaginationInfo? = null,
    val maxWidthCm: Int? = null,
    val maxHeightCm: Int? = null,
)