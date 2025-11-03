package com.ssafy.yeobaeg.domain.model

data class Pagination(
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean
)