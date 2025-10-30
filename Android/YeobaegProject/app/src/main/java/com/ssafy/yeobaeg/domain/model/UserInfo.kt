package com.ssafy.yeobaeg.domain.model

data class UserInfo (
    val userId: Long,
    val email: String,
    val nickname: String,
    val userType: UserRole,
    val profileImageUrl: String?,
    val birthDate: String?,
    val gender: Gender
)