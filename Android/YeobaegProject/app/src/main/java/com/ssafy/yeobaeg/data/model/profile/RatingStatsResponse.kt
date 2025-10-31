package com.ssafy.yeobaeg.data.model.profile

import kotlinx.serialization.Serializable

/**
 * 프로필 리뷰 정보
 */
@Serializable
data class RatingStatsResponse(
    val totalReviews: Int,
    val averageRating: Double
)