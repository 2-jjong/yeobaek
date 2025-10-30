package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.Artist
import com.ssafy.yeobaeg.domain.model.Cafe
import com.ssafy.yeobaeg.domain.model.Gender
import com.ssafy.yeobaeg.domain.model.Profile
import com.ssafy.yeobaeg.domain.model.RatingStats
import com.ssafy.yeobaeg.domain.model.UserInfo
import com.ssafy.yeobaeg.domain.model.UserRole

data class GetProfileResponseDto(
    val user: UserDto,
    val cafe: CafeDto?,
    val artist: ArtistDto?
)

data class UserDto(
    val userId: Long,
    val email: String,
    val nickname: String,
    val userType: String, // 서버는 문자열을 주므로 String으로 받아야 함
    val profileImageUrl: String?,
    val birthDate: String?,
    val gender: Gender
)

data class CafeDto(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHours: String,
    val ratingStats: RatingStatsDto
)

data class ArtistDto(
    val introduction: String,
    val artworkCount: Int,
    val exhibitingCount: Int,
    val ratingStats: RatingStatsDto
)

data class RatingStatsDto(
    val totalReviews: Int,
    val averageRating: Double
)

// DTO → Domain 변환
fun GetProfileResponseDto.toDomain(): Profile = Profile(
    userInfo = user.toDomain(),
    cafeInfo = cafe?.toDomain(),
    artistInfo = artist?.toDomain()
)

fun UserDto.toDomain(): UserInfo = UserInfo(
    userId = userId,
    email = email,
    nickname = nickname,
    userType = UserRole.valueOf(userType),
    profileImageUrl = profileImageUrl,
    birthDate = birthDate,
    gender = gender
)

fun CafeDto.toDomain(): Cafe = Cafe(
    cafeId = cafeId,
    cafeName = cafeName,
    address = address,
    description = description,
    contact = contact,
    businessHours = businessHours,
    ratingStats = ratingStats.toDomain()
)

fun ArtistDto.toDomain(): Artist = Artist(
    introduction = introduction,
    artworkCount = artworkCount,
    exhibitingCount = exhibitingCount,
    ratingStats = ratingStats.toDomain()
)

fun RatingStatsDto.toDomain(): RatingStats = RatingStats(
    totalReviews = totalReviews,
    averageRating = averageRating
)