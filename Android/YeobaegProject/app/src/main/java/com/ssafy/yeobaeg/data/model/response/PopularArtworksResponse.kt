package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.art.PopularArtwork
import kotlinx.serialization.Serializable

@Serializable
data class PopularArtworksData(
    val artworks: List<PopularArtworkDto>
)

@Serializable
data class PopularArtworkDto(
    val artworkId: Long,
    val title: String,
    val thumbnailUrl: String,
    val artistName: String,
    val artistProfileImageUrl: String,
    val price: Int,
    val category: String,
    val widthCm: Int,
    val heightCm: Int,
    val status: String,
    val bookmarkCount: Int,
    val cafeName: String? = null
)

fun PopularArtworkDto.toDomain() = PopularArtwork(
    artworkId = artworkId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    artistName = artistName,
    artistProfileImageUrl = artistProfileImageUrl,
    price = price,
    category = category,
    widthCm = widthCm,
    heightCm = heightCm,
    status = when (status) {
        "AVAILABLE" -> SlotStatus.AVAILABLE
        "EXHIBITING" -> SlotStatus.EXHIBITING
        else -> SlotStatus.AVAILABLE
    },
    bookmarkCount = bookmarkCount,
    cafeName = cafeName
)

