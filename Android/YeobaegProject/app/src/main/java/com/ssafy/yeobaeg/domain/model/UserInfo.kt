package com.ssafy.yeobaeg.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo (
    val userId: Long = 0L,
    val email: String = "",
    val nickname: String="",
    val userType: UserRole= UserRole.GENERAL,
    val profileImageUrl: String?="",
    val birthDate: String?="",
    val gender: Gender= Gender.MALE
)