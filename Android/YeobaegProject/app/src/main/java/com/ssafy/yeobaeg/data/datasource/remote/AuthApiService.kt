package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.auth.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<BaseResponse<Unit?>>
}

