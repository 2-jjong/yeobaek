package com.ssafy.yeobaeg.domain.model.profile

import com.ssafy.yeobaeg.domain.model.profile.RatingStats

data class ArtistInfo(
    val introduction: String="",
    val artworkCount: Int=0,
    val exhibitingCount: Int=0,
    val ratingStats: RatingStats= RatingStats()
)