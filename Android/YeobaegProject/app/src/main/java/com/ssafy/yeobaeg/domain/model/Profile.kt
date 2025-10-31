package com.ssafy.yeobaeg.domain.model

data class Profile (
    val userInfo: UserInfo,
    val cafeInfo: CafeInfo?=null,
    val artistInfo: ArtistInfo?=null
)
