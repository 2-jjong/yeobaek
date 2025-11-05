package com.ssafy.yeobaeg.domain.model.profile

import com.ssafy.yeobaeg.domain.model.profile.RatingStats

data class ArtistInfo(
    val introduction: String,
    val artworkCount: Int,
    val exhibitingCount: Int,
    val ratingStats: RatingStats
)