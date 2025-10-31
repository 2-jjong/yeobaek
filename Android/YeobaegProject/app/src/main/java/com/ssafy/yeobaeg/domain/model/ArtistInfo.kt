package com.ssafy.yeobaeg.domain.model

data class ArtistInfo(
    val introduction: String,
    val artworkCount: Int,
    val exhibitingCount: Int,
    val ratingStats: RatingStats
)