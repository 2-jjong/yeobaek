package com.ssafy.yeobaeg.domain.model

data class CafeInfo(
    val cafeId: Long=0L,
    val cafeName: String="",
    val address: String="",
    val description: String="",
    val contact: String="",
    val businessHours: String="",
    val ratingStats: RatingStats= RatingStats()
)