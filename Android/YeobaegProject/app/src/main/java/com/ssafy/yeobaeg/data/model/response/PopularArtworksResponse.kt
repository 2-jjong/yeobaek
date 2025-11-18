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
    val artistName: String? = null,
    val artistProfileImageUrl: String? = null,
    val price: Int,
    val category: String,
    val widthCm: Int? = null,
    val heightCm: Int? = null,
    val status: String? = null,
    val bookmarkCount: Int,
    val cafeName: String? = null
)

fun PopularArtworkDto.toDomain() = PopularArtwork(
    artworkId = artworkId,
    title = title,
    thumbnailUrl = thumbnailUrl,
    artistName = artistName ?: "알 수 없음",
    artistProfileImageUrl = artistProfileImageUrl ?: "",
    price = price,
    category = when (category) {
        "LANDSCAPE"  -> "풍경화"
        "PORTRAIT"   -> "인물화"
        "STILL_LIFE" -> "정물화"
        "HISTORY" -> "역사화"
        "GENRE"      -> "장르화"
        else     -> "기타"
    },
    widthCm = widthCm ?: 0,
    heightCm = heightCm ?: 0,
    status = when (status) {
        "AVAILABLE" -> SlotStatus.AVAILABLE
        "EXHIBITING" -> SlotStatus.EXHIBITING
        null -> SlotStatus.AVAILABLE
        else -> SlotStatus.AVAILABLE
    },
    bookmarkCount = bookmarkCount,
    cafeName = cafeName
)

