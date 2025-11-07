package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.art.request.ArtworkRegisterRequest
import com.ssafy.yeobaeg.data.model.response.ArtSearchResponse
import com.ssafy.yeobaeg.data.model.response.ArtworkDetailResponse
import com.ssafy.yeobaeg.data.model.response.PopularArtworksData
import com.ssafy.yeobaeg.data.model.response.ToggleBookmarkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtworkApiService {

    @GET("artworks")
    suspend fun getArtworks(
        @Query("keyword") keyword: String? = null,
        @Query("categories") categories: List<String>? = null,
        @Query("minWidth") minWidth: Int = 0,
        @Query("maxWidth") maxWidth: Int = Integer.MAX_VALUE,
        @Query("minHeight") minHeight: Int = 0,
        @Query("maxHeight") maxHeight: Int = Integer.MAX_VALUE,
        @Query("sortBy") sortBy: String = "LATEST",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): BaseResponse<ArtSearchResponse>


    @GET("artworks/{artworkId}")
    suspend fun getArtworkDetail(
        @Path("artworkId") artworkId: Long
    ): BaseResponse<ArtworkDetailResponse>

    @POST("artworks/{artworkId}/bookmarks")
    suspend fun toggleBookmark(
        @Path("artworkId") artworkId: Long
    ): BaseResponse<ToggleBookmarkResponse>

    @GET("artworks/popular")
    suspend fun getPopularArtworks(
        @Query("size") size: Int = 10
    ): BaseResponse<PopularArtworksData>

    @POST("artworks")
    suspend fun registerArtwork(
        @Body request: ArtworkRegisterRequest
    ) : BaseResponse<String>
}

