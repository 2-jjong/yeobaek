package com.ssafy.yeobaeg.data.model.response

import com.ssafy.yeobaeg.domain.model.PaginationInfo
import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    val currentPage: Int,
    val hasNext: Boolean,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int
)

fun Pagination.toDomain() = PaginationInfo(
    currentPage = currentPage,
    hasNext = hasNext,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages
)