package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.profile.GetProfileResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApiService {
    @GET("users/me")
    suspend fun getProfile(): Response<BaseResponse<GetProfileResponseDto>>

}