package com.ssafy.yeobaeg.domain.model

data class BookmarkArtwork(
    val bookmarkArtworks: List<BookmarkArtworkSummary>,
    val pagination: Pagination
)