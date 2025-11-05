package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe

interface CafeRepository {

    suspend fun registerCafe(cafe: RegisterCafe)

    suspend fun getPopularCafes(size: Int = 10): Result<List<CafeSummary>>
}