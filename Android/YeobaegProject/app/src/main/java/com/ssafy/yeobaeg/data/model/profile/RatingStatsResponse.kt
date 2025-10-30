package com.ssafy.yeobaeg.data.model.profile

/**
 * 프로필 리뷰 정보
 */
data class RatingStatsResponse(
    val totalReviews: Int,
    val averageRating: Double
)