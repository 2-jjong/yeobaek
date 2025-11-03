package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.art.ArtSearchResult
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.art.ArtworkSummary
import kotlinx.serialization.Serializable

@Serializable
data class ArtSearchResponse(
    val artists: List<Artist>,
    val artworks: List<Artwork>,
    val pagination: PaginationResponse
)

@Serializable
data class Artist(
    val artistName: String,
    val artworkCount: Int,
    val introduction: String,
    val profileImageUrl: String,
    val userId: Int
)

@Serializable
data class Artwork(
    val artistName: String,
    val artistProfileImageUrl: String,
    val artworkId: Int,
    val bookmarkCount: Int,
    val cafeName: String? = null,
    val category: String,
    val heightCm: Int,
    val price: Int,
    val status: String,
    val thumbnailUrl: String,
    val title: String,
    val widthCm: Int
)

fun ArtSearchResponse.toDomain() = ArtSearchResult(
    artistSummaries = artists.map { it.toDomain() },
    artworkSummaries =  artworks.map { it.toDomain() },
    paginationInfo = pagination.toDomain()
)
fun Artist.toDomain() = ArtistSummary(
    artistId = userId.toLong(),
    artistName = artistName,
    profileImageUrl = profileImageUrl,
    introduction = introduction,
    artworkCount = artworkCount
)

fun Artwork.toDomain() = ArtworkSummary(
    artworkId = artworkId.toLong(),
    title = title,
    thumbnailUrl = thumbnailUrl,
    price = price,
    category = category,
    widthCm = widthCm,
    heightCm = heightCm,
    status = when (status) {
        "AVAILABLE" -> SlotStatus.AVAILABLE
        "EXHIBITING" -> SlotStatus.ONGOING
        else -> SlotStatus.RESERVED
    },
    bookmarkCount = bookmarkCount
)