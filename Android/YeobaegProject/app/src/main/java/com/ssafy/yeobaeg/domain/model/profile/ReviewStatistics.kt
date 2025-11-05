package com.ssafy.yeobaeg.domain.model.profile

import kotlinx.serialization.Serializable

data class ReviewStats(
    val statists: ReviewStat
)

data class ReviewStat(
    val userId: Long,
    val userName:String,
    val totalReviews:Int,
    val okayCount: Int,
    val goodCount: Int,
    val badCount: Int,
    val goodPercentage:Double
)