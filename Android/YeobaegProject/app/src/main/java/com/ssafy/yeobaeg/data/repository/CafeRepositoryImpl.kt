package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.CafeApiService
import com.ssafy.yeobaeg.data.model.request.toDto
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import javax.inject.Inject

class CafeRepositoryImpl @Inject constructor(
    private val cafeApiService: CafeApiService
) : CafeRepository {

    override suspend fun registerCafe(cafe: RegisterCafe) {
        runCatching { cafeApiService.registerCafe(cafe.toDto()) }
    }

    override suspend fun getPopularCafes(size: Int): Result<List<CafeSummary>> {
        return runCatching {
            val response = cafeApiService.getPopularCafes(size)
            response.data?.cafes?.map { it.toDomain() } ?: emptyList()
        }
    }
}