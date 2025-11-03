package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.ProfileApiService
import com.ssafy.yeobaeg.data.model.profile.toDomain
import com.ssafy.yeobaeg.domain.model.BookmarkArtwork
import com.ssafy.yeobaeg.domain.model.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.Profile
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

    override suspend fun getBookmarkArtworkList(page:Int, size:Int): Result<BookmarkArtwork> {
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

    override suspend fun getBookmarkCafeList(page:Int, size:Int): Result<BookmarkCafe> {
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
}