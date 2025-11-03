package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkArtworkSummaryResponse(
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