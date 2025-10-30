package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.Artist
import com.ssafy.yeobaeg.domain.model.Cafe
import com.ssafy.yeobaeg.domain.model.Profile
import com.ssafy.yeobaeg.domain.model.RatingStats
import com.ssafy.yeobaeg.domain.model.UserInfo
import com.ssafy.yeobaeg.domain.model.UserRole

data class GetProfileResponseDto(
    val user: UserProfileResponse,
    val cafe: CafeProfileResponse?,
    val artist: ArtistProfileResponse?
)

// DTO → Domain 변환
fun GetProfileResponseDto.toDomain(): Profile = Profile(
    userInfo = user.toDomain(),
    cafeInfo = cafe?.toDomain(),
    artistInfo = artist?.toDomain()
)

fun UserProfileResponse.toDomain(): UserInfo = UserInfo(
    userId = userId,
    email = email,
    nickname = nickname,
    userType = UserRole.valueOf(userType),
    profileImageUrl = profileImageUrl,
    birthDate = birthDate,
    gender = gender
)

fun CafeProfileResponse.toDomain(): Cafe = Cafe(
    cafeId = cafeId,
    cafeName = cafeName,
    address = address,
    description = description,
    contact = contact,
    businessHours = businessHours,
    ratingStats = ratingStats.toDomain()
)

fun ArtistProfileResponse.toDomain(): Artist = Artist(
    introduction = introduction,
    artworkCount = artworkCount,
    exhibitingCount = exhibitingCount,
    ratingStats = ratingStats.toDomain()
)

fun RatingStatsResponse.toDomain(): RatingStats = RatingStats(
    totalReviews = totalReviews,
    averageRating = averageRating
)