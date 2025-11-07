package com.ssafy.yeobaeg.domain.model.profile

data class BookmarkArtworkSummary(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String,
    val artistName: String,
    val artistProfileImageUrl: String?,
    val price: Int,
    val category: String,
    val widthCm: Int,
    val heightCm: Int,
    val status: String,
    val bookmarkCount: Int
)