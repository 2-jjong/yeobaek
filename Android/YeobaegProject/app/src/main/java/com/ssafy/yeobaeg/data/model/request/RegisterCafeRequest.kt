package com.ssafy.yeobaeg.data.model.request

import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import kotlinx.serialization.Serializable

@Serializable
data class RegisterCafeRequest(
    val address: String,
    val businessHoursEnd: String,
    val businessHoursStart: String,
    val cafeName: String,
    val categories: List<String>,
    val contact: String,
    val dailyVisitors: String,
    val description: String,
    val images: List<Image>
)



fun RegisterCafe.toDto(): RegisterCafeRequest =
    RegisterCafeRequest(
        address = address,
        businessHoursStart = businessHoursStart,
        businessHoursEnd = businessHoursEnd,
        cafeName = cafeName,
        categories = categories.map { it.label },
        contact = contact,
        dailyVisitors = dailyVisitors.label,
        description = description,
        images = images.map {
            Image(
                displayOrder = it.displayOrder,
                imageUrl = it.imageUrl,
                isPrimary = it.isPrimary
            )
        }
    )