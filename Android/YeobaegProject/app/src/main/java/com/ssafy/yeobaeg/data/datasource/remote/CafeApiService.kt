package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.request.RegisterCafeRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CafeApiService {

    @POST("cafes")
    suspend fun registerCafe(
        @Body request: RegisterCafeRequest
    ) : BaseResponse<String>
}