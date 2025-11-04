package com.ssafy.yeobaeg.domain.model.cafe

/**
 * 카페 요약 정보 (지도에서 보여줄 카페 정보)
 */
data class CafeSummary(
    val cafeId: Long,
    val cafeName: String,
    val address: String,
    val primaryImageUrl: String?,
    val availableSlotCount: Int,
    val totalSlotCount: Int,
    val distance: Double,
    val categories: List<String>,
    val latitude: Double? = null,
    val longitude: Double? = null
)

