package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.request.RegisterCafeRequest
import com.ssafy.yeobaeg.data.model.response.PopularCafesData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CafeApiService {

    @POST("cafes")
    suspend fun registerCafe(
        @Body request: RegisterCafeRequest
    ) : BaseResponse<String>

    @GET("cafes/popular")
    suspend fun getPopularCafes(
        @Query("size") size: Int = 10
    ): BaseResponse<PopularCafesData>
}