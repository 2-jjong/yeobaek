package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.response.ArtSearchResponse
import retrofit2.http.GET
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


}