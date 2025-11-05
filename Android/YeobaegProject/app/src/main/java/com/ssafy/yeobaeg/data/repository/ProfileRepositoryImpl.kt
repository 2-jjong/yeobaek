package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.ProfileApiService
import com.ssafy.yeobaeg.data.model.profile.ReviewStatistics
import com.ssafy.yeobaeg.data.model.profile.UpdateProfileRequest
import com.ssafy.yeobaeg.data.model.profile.toDomain
import com.ssafy.yeobaeg.domain.model.profile.BookmarkArtwork
import com.ssafy.yeobaeg.domain.model.profile.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.profile.Profile
import com.ssafy.yeobaeg.domain.model.profile.ReviewStats
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import javax.inject.Inject

private const val TAG = "ProfileRepositoryImpl"

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfile(): Result<Profile> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                val profileDto = response.body()?.data
                Log.d(TAG, "getProfile: $profileDto")
                if (profileDto != null) {
                    Result.success(profileDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get profile"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getProfile() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookmarkArtworkList(page: Int, size: Int): Result<BookmarkArtwork> {
        return try {
            val response = api.getBookmarkArtworkList(page = page, size = size)
            if (response.isSuccessful) {
                val bookmarkDto = response.body()?.data
                Log.d(TAG, "getBookmarkArtworksList: $bookmarkDto")
                if (bookmarkDto != null) {
                    Result.success(bookmarkDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get bookmark artworks"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "getBookmarkArtworksList() error: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun getBookmarkCafeList(page: Int, size: Int): Result<BookmarkCafe> {
        return try {
            val response = api.getBookmarkCafeList(page = page, size = size)
            if (response.isSuccessful) {
                val bookmarkDto = response.body()?.data
                if (bookmarkDto != null) {
                    Result.success(bookmarkDto.toDomain())
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Failed to get bookmark cafes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(
        nickname: String?,
        profileImageUrl: String?,
        introduction: String?
    ): Result<Unit> {
        return runCatching {
            val response = api.updateProfileNickname(
                request = UpdateProfileRequest(
                    nickname = nickname,
                    profileImageUrl = profileImageUrl,
                    introduction = introduction
                )
            )

            if (!response.isSuccessful) {
                throw Exception("프로필 수정 실패: ${response.code()} ${response.message()}")
            }
            Unit
        }
    }

    override suspend fun getReviewStatistics(userId: Long): Result<ReviewStats> {
        return runCatching {
            val response = api.getReviewStatistics(userId)
            if (!response.isSuccessful) {
                throw Exception("리뷰 통계 조회 실패: ${response.code()} ${response.message()}")
            }

            val body = response.body()?.data
                ?: throw Exception("Response body is null")

            body.toDomain()
        }
    }

}