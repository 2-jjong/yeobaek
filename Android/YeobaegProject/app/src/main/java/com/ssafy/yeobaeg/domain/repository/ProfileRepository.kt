package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
}

