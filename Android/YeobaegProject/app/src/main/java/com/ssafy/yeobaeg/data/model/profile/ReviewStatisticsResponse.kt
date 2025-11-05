package com.ssafy.yeobaeg.data.model.profile
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.profile.ReviewStat
import com.ssafy.yeobaeg.domain.model.profile.ReviewStats
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class ReviewStatisticsResponse(
    val statistics: ReviewStatistics
)

@Serializable
data class ReviewStatistics(
    val userId: Long,
    val userName:String,
    val totalReviews:Int,
    val okayCount: Int,
    val goodCount: Int,
    val badCount: Int,
    val goodPercentage:Double
)

fun ReviewStatisticsResponse.toDomain(): ReviewStats = ReviewStats(
    statists = statistics.toDomain()
)

fun ReviewStatistics.toDomain(): ReviewStat = ReviewStat(
    userId = userId,
    userName = userName,
    totalReviews = totalReviews,
    goodCount = goodCount,
    okayCount = okayCount,
    badCount = badCount,
    goodPercentage = goodPercentage
)
