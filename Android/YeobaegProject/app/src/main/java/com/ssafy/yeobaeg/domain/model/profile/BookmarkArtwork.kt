package com.ssafy.yeobaeg.domain.model.profile

import com.ssafy.yeobaeg.domain.model.Pagination

data class BookmarkArtwork(
    val bookmarkArtworks: List<BookmarkArtworkSummary>,
    val pagination: Pagination
)