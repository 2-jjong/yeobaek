package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.request.CreateDisplayWallRequest
import com.ssafy.yeobaeg.data.model.request.RegisterCafeRequest
import com.ssafy.yeobaeg.data.model.request.UpdateSlotRequest
import com.ssafy.yeobaeg.data.model.response.CafeCategoryResponse
import com.ssafy.yeobaeg.data.model.response.CafeOwnerHomeResponse
import com.ssafy.yeobaeg.data.model.response.DisplaySlotResponse
import com.ssafy.yeobaeg.data.model.response.PopularCafesData
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serial

interface CafeApiService {

    @POST("cafes")
    suspend fun registerCafe(
        @Body request: RegisterCafeRequest
    ) : BaseResponse<String>

    @GET("cafes/popular")
    suspend fun getPopularCafes(
        @Query("size") size: Int = 10
    ): BaseResponse<PopularCafesData>

    @GET("cafes/me")
    suspend fun getMyCafe(): BaseResponse<CafeOwnerHomeResponse>

    @POST("cafes/walls")
    suspend fun createCafeWall(
        @Body request: List<CreateDisplayWallRequest>
    ): BaseResponse<Unit>

    @PATCH("cafes/{cafeId}/slots/{slotId}")
    suspend fun updateSlotInfo(
        @Path("cafeId") cafeId: Long,
        @Path("slotId") slotId: Long,
        @Body request: UpdateSlotRequest,
    ): BaseResponse<Unit>

    @GET("cafes/categories")
    suspend fun getCafeCategories(): BaseResponse<CafeCategoryResponse>
}
