package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.ProfileApiService
import com.ssafy.yeobaeg.data.model.profile.toDomain
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
}