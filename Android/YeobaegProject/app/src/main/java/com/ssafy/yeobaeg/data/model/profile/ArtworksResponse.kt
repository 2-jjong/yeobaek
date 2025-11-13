package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.profile.ArtworkSummary
import com.ssafy.yeobaeg.domain.model.profile.Artwork
import kotlinx.serialization.Serializable

@Serializable
data class ArtworksResponse(
    val artworks: List<ArtworkSummaryResponse>,
    val pagination: PaginationResponse
)

fun ArtworksResponse.toDomain(): Artwork {
    return Artwork(
        bookmarkArtworks = artworks.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}

fun ArtworkSummaryResponse.toDomain(): ArtworkSummary {
    return ArtworkSummary(
        artworkId = artworkId,
        title = title,
        thumbnailUrl = thumbnailUrl,
        artistName = artistName,
        artistProfileImageUrl = artistProfileImageUrl,
        price = price,
        category = category,
        widthCm = widthCm,
        heightCm = heightCm,
        isExhibiting = isExhibiting,
        bookmarkCount = bookmarkCount
    )
}
