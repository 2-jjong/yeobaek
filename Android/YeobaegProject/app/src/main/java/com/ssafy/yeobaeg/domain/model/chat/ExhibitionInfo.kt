package com.ssafy.yeobaeg.domain.model.chat

data class ExhibitionInfo(
    val cafeId: Long,
    val artworkId: Long,
    val slotId: Long,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String,
    val agreedCommissionRate: Double,
    val requirements: String,
    val systemMessageType: String,
)