package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ExhibitionInfo
import kotlinx.serialization.Serializable

@Serializable
data class ExhibitionInfoResponse(
    val cafeId: Long,
    val artworkId: Long,
    val slotId: Long,
    val exhibitionStartDate: String,
    val exhibitionEndDate: String,
    val agreedCommissionRate: Double,
    val requirements: String,
    val systemMessageType: String,
)

fun ExhibitionInfoResponse.toDomain(): ExhibitionInfo {
    return ExhibitionInfo(
        cafeId = cafeId,
        artworkId = artworkId,
        slotId = slotId,
        exhibitionStartDate = exhibitionStartDate,
        exhibitionEndDate = exhibitionEndDate,
        agreedCommissionRate = agreedCommissionRate,
        requirements = requirements,
        systemMessageType = systemMessageType
    )
}