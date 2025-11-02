package com.ssafy.yeobaeg.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val displayOrder: Int,
    val imageUrl: String,
    val isPrimary: Boolean = false
)