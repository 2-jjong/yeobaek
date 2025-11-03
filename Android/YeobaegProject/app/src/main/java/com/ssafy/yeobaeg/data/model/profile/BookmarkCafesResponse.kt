package com.ssafy.yeobaeg.data.model.profile

import com.ssafy.yeobaeg.domain.model.BookmarkArtworkSummary
import com.ssafy.yeobaeg.domain.model.BookmarkArtwork
import com.ssafy.yeobaeg.domain.model.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.BookmarkCafeSummary
import kotlinx.serialization.Serializable
import kotlin.Long

@Serializable
data class BookmarkCafesResponse(
    val cafes: List<BookmarkCafeSummaryResponse>,
    val pagination: PaginationResponse
)

fun BookmarkCafesResponse.toDomain(): BookmarkCafe {
    return BookmarkCafe(
        bookmarkCafes = cafes.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}

fun BookmarkCafeSummaryResponse.toDomain(): BookmarkCafeSummary {
    return BookmarkCafeSummary(
        cafeId = cafeId,
        address = address,
        primaryImageUrl = primaryImageUrl,
        cafeName = cafeName,
        categories = categories,
        availableSlotCount = availableSlotCount,
        totalSlotCount = totalSlotCount,
    )
}

