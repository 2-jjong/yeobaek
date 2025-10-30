package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.Gender

/**
 * 일반 프로필 정보
 */
data class UserProfileResponse(
    val userId: Long,
    val email: String,
    val nickname: String,
    val userType: String,
    val profileImageUrl: String?,
    val birthDate: String?,
    val gender: Gender
)