package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.profile.BookmarkArtworksResponse
import com.ssafy.yeobaeg.data.model.profile.BookmarkCafesResponse
import com.ssafy.yeobaeg.data.model.profile.GetProfileResponseDto
import com.ssafy.yeobaeg.data.model.profile.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface ProfileApiService {
    @GET("users/me")
    suspend fun getProfile(
    ): Response<BaseResponse<GetProfileResponseDto>>

    @GET("artworks/bookmarks")
    suspend fun getBookmarkArtworkList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<BookmarkArtworksResponse>>

    @GET("cafes/bookmarks")
    suspend fun getBookmarkCafeList(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<BaseResponse<BookmarkCafesResponse>>

    @PATCH("users/me")
    suspend fun updateProfileNickname(
        @Body request: UpdateProfileRequest
    ): Response<BaseResponse<Unit>>

}