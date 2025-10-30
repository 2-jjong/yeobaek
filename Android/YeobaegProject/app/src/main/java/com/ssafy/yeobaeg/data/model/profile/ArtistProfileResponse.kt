package com.ssafy.yeobaeg.data.model.profile

/**
 * 프로필 아티스트 정보
 */
data class ArtistProfileResponse(
    val introduction: String,
    val artworkCount: Int,
    val exhibitingCount: Int,
    val ratingStats: RatingStatsResponse
)