package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.BookmarkArtworkSummary
import com.ssafy.yeobaeg.domain.model.BookmarkArtwork
import kotlinx.serialization.Serializable

@Serializable
data class BookmarkArtworksResponse(
    val artworks: List<BookmarkArtworkSummaryResponse>,
    val pagination: PaginationResponse
)

fun BookmarkArtworksResponse.toDomain(): BookmarkArtwork {
    return BookmarkArtwork(
        bookmarkArtworks = artworks.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}

fun BookmarkArtworkSummaryResponse.toDomain(): BookmarkArtworkSummary {
    return BookmarkArtworkSummary(
        artworkId = artworkId,
        title = title,
        thumbnailUrl = thumbnailUrl,
        artistName = artistName,
        artistProfileImageUrl = artistProfileImageUrl,
        price = price,
        category = category,
        widthCm = widthCm,
        heightCm = heightCm,
        status = status,
        bookmarkCount = bookmarkCount
    )
}
