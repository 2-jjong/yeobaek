package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ArtworkApiService
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import javax.inject.Inject

class ArtRepositoryImpl @Inject constructor(
    private val artworkApiService: ArtworkApiService
) : ArtRepository {

    override suspend fun getArtworks(category: String) =
        runCatching {
            val response = artworkApiService.getArtworks()
            response.data?.artworks?.map { it.toDomain() }
                ?: throw Exception(response.message)
        }

    override suspend fun searchArt(
        keyword: String,
        categories: List<String>,
        minWidth: Int,
        maxWidth: Int,
        minHeight: Int,
        maxHeight: Int,
        sortBy: String,
        page: Int,
        size: Int
    ) = runCatching {
        val response = artworkApiService.getArtworks(
            keyword = keyword,
            categories = categories,
            minWidth = minWidth,
            maxWidth = maxWidth,
            minHeight = minHeight,
            maxHeight = maxHeight,
            sortBy = sortBy,
            page = page,
            size = size
        )
        response.data?.toDomain() ?: throw Exception(response.message)
    }

    override suspend fun getArtworkDetail(artWorkId: Long) =
        runCatching {
            val response = artworkApiService.getArtworkDetail(artWorkId)
            response.data?.artwork?.toDomain() ?: throw Exception(response.message)
        }

    override suspend fun toggleBookmark(artWorkId: Long) =
        runCatching {
            val response = artworkApiService.toggleBookmark(artWorkId)
            response.data?.isBookmarked ?: throw Exception(response.message)
        }

    override suspend fun getPopularArtworks(size: Int) =
        runCatching {
            val response = artworkApiService.getPopularArtworks(size)
            response.data?.artworks?.map { it.toDomain() } ?: emptyList()
        }
}