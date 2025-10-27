package com.ssafy.yeobaeg.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole
)

