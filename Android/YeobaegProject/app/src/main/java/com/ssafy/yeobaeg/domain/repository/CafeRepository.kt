package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.cafe.CafeCategory
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwnerInfo
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe

interface CafeRepository {

    suspend fun registerCafe(cafe: RegisterCafe)

    suspend fun getPopularCafes(size: Int = 10): Result<List<CafeSummary>>

    suspend fun registerCafeWall(registerWall: List<DisplaySlot>): Result<Unit>

    suspend fun getMyCafe(): Result<CafeOwnerInfo>

    suspend fun updateSlotInfo(
        cafeId: Long,
        slotId: Long,
        commissionRate: Int?,
        requirements: String?
    ): Result<Unit>

    suspend fun getCafeCategories(): Result<List<CafeCategory>>

}