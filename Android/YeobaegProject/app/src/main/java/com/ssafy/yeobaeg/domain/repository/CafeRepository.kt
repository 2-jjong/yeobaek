package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe

interface CafeRepository {

    suspend fun registerCafe(cafe: RegisterCafe)
}