package com.ssafy.yeobaeg.domain.model.art

import com.ssafy.yeobaeg.domain.model.SlotStatus

data class ArtworkSummary(
    val artworkId: Long = -1L,
    val title: String = "",
    val thumbnailUrl: String = "",
    val price: Int = 0,
    val category: String = "",
    val widthCm: Int = 0,
    val heightCm: Int = 0,
    val status: SlotStatus = SlotStatus.UNAVAILABLE,
    val bookmarkCount: Int = 0
)
