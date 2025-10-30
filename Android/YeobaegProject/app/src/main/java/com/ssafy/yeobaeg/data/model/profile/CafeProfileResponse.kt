package com.ssafy.yeobaeg.data.model.profile

/**
 * 프로필 카페 정보
 */
data class CafeProfileResponse(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHours: String,
    val ratingStats: RatingStatsResponse
)