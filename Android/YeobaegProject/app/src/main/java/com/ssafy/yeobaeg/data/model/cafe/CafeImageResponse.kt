package com.ssafy.yeobaeg.data.model.cafe

import com.ssafy.yeobaeg.domain.model.cafe.CafeImage
import kotlinx.serialization.Serializable

@Serializable
data class CafeImageDto(
    val imageId: Long,
    val imageUrl: String,
    val isPrimary: Boolean,
    val displayOrder: Int
)

fun CafeImageDto.toDomain(): CafeImage {
    return CafeImage(
        imageId = imageId,
        imageUrl = imageUrl,
        isPrimary = isPrimary,
        displayOrder = displayOrder
    )
}

