package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.profile.BookmarkArtworkSummary
import com.ssafy.yeobaeg.domain.model.profile.BookmarkArtwork
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
