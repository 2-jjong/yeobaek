package com.ssafy.yeobaeg.domain.model.art

import com.ssafy.yeobaeg.domain.model.Pagination

data class ArtSearchResult(
    val artworkSummaries: List<ArtworkSummary>,
    val artistSummaries: List<ArtistSummary>,
    val paginationInfo: Pagination
)
