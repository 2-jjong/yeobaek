package com.ssafy.yeobaeg.domain.model

data class Profile (
    val userInfo: UserInfo,
    val cafeInfo: Cafe?=null,
    val artistInfo: Artist?=null
)

data class UserInfo (
    val userId: Long,
    val email: String,
    val nickname: String,
    val userType: UserRole,
    val profileImageUrl: String?,
    val birthDate: String?,
    val gender: Gender
)
data class Cafe(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHours: String,
    val ratingStats: RatingStats
)
data class Artist(
    val introduction: String,
    val artworkCount: Int,
    val exhibitingCount: Int,
    val ratingStats: RatingStats
)

data class RatingStats(
    val totalReviews: Int,
    val averageRating: Double
)