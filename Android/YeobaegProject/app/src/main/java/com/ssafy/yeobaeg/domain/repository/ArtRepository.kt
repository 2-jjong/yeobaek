package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.art.ArtSearchResult
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.model.artist.ArtworkRegister
import com.ssafy.yeobaeg.domain.model.art.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.art.PopularArtwork

interface ArtRepository {

    suspend fun getArtworks(category: String): Result<List<ArtworkSummary>>

    suspend fun searchArt(
        keyword: String = "",
        categories: List<String> = emptyList(),
        minWidth: Int = 0,
        maxWidth: Int = Integer.MAX_VALUE,
        minHeight: Int = 0,
        maxHeight: Int = Integer.MAX_VALUE,
        sortBy: String = "LATEST",
        page: Int = 0,
        size: Int = 20
    ): Result<ArtSearchResult>

    suspend fun getArtworkDetail(artWorkId: Long): Result<ArtworkDetail>

    suspend fun toggleBookmark(artWorkId: Long): Result<Boolean>

    suspend fun getPopularArtworks(size: Int = 10): Result<List<PopularArtwork>>
    suspend fun registerArtwork(request: ArtworkRegister)

}