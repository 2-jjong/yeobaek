package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.CafeApiService
import com.ssafy.yeobaeg.data.model.request.UpdateSlotRequest
import com.ssafy.yeobaeg.data.model.request.toDto
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import javax.inject.Inject
import kotlin.collections.map

class CafeRepositoryImpl @Inject constructor(
    private val cafeApiService: CafeApiService
) : CafeRepository {

    override suspend fun registerCafe(cafe: RegisterCafe) = runCatching {
        val response = cafeApiService.registerCafe(cafe.toDto())
        if (response.code != "S0000") throw Exception(response.message) else Unit
    }

    override suspend fun getPopularCafes(size: Int): Result<List<CafeSummary>> {
        return runCatching {
            val response = cafeApiService.getPopularCafes(size)
            response.data?.cafes?.map { it.toDomain() } ?: emptyList()
        }
    }

    override suspend fun registerCafeWall(registerWall: List<DisplaySlot>) =
        runCatching {
            val response = cafeApiService.createCafeWall(registerWall.map { it.toDto() })
            response.data ?: throw Exception(response.message)
        }

    override suspend fun getMyCafe() = runCatching {
        val response = cafeApiService.getMyCafe()
        response.data?.myCafe?.toDomain() ?: throw (Exception(response.message))
    }

    override suspend fun updateSlotInfo(
        cafeId: Long,
        slotId: Long,
        commissionRate: Int?,
        requirements: String?
    ) = runCatching {
        val response = cafeApiService.updateSlotInfo(
            cafeId, slotId,
            UpdateSlotRequest(commissionRate, requirements)
        )
        response.data ?: throw Exception(response.message)
    }

    override suspend fun getCafeCategories() = runCatching {
        val response = cafeApiService.getCafeCategories()
        response.data?.categories?.map { it.toDomain() } ?: throw Exception(response.message)
    }
}