package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.profile.BookmarkArtwork
import com.ssafy.yeobaeg.domain.model.profile.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.profile.Profile
import com.ssafy.yeobaeg.domain.model.profile.ReviewStats

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun getSomeoneProfile(userId:Long): Result<Profile>
    suspend fun getBookmarkArtworkList(page: Int, size: Int): Result<BookmarkArtwork>
    suspend fun getBookmarkCafeList(page: Int, size: Int): Result<BookmarkCafe>
    suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
        introduction: String?
    ):Result<Unit>
    suspend fun getReviewStatistics(userId:Long):Result<ReviewStats>
}

