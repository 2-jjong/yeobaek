package com.ssafy.yeobaeg.domain.model

data class PaginationInfo(
    val currentPage: Int,
    val hasNext: Boolean,
    val pageSize: Int,
    val totalElements: Int,
    val totalPages: Int
)