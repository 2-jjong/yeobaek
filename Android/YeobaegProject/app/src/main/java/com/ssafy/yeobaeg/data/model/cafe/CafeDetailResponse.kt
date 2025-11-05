package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import kotlinx.serialization.Serializable

@Serializable
data class CafeDetailData(
    val cafeDetail: CafeDetailDto
)

@Serializable
data class CafeDetailDto(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val description: String,
    val contact: String,
    val businessHoursStart: String,
    val businessHoursEnd: String,
    val dailyVisitors: String,
    val viewCount: Int,
    val isBookmarked: Boolean,
    val categories: List<String>,
    val images: List<CafeImageDto>,
    val slots: List<SlotDetailDto>,
    val cafeOwner: CafeOwnerDto
)

fun CafeDetailDto.toDomain(): CafeDetail {
    return CafeDetail(
        cafeId = cafeId,
        cafeName = cafeName,
        address = address,
        description = description,
        contact = contact,
        businessHoursStart = businessHoursStart,
        businessHoursEnd = businessHoursEnd,
        dailyVisitors = dailyVisitors,
        viewCount = viewCount,
        isBookmarked = isBookmarked,
        categories = categories,
        images = images.map { it.toDomain() },
        slots = slots.map { it.toDomain() },
        cafeOwner = cafeOwner.toDomain()
    )
}


