package com.ssafy.yeobaeg.domain.model.cafe

data class CafeImage(
    val imageId: Long,
    val imageUrl: String,
    val isPrimary: Boolean,
    val displayOrder: Int
)

