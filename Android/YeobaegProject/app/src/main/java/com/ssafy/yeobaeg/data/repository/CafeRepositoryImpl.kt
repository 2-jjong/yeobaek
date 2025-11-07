package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.CafeApiService
import com.ssafy.yeobaeg.data.model.cafe.toDomain
import com.ssafy.yeobaeg.data.model.request.toDto
import com.ssafy.yeobaeg.data.model.response.toDomain
import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeListResult
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

    override suspend fun getCafes(
        keyword: String?,
        latitude: Double,
        longitude: Double,
        filter: CafeFilter?,
        page: Int,
        size: Int
    ): Result<CafeListResult> {
        return runCatching {
            val response = cafeApiService.getCafes(
                keyword = keyword,
                latitude = latitude,
                longitude = longitude,
                minWidth = filter?.minWidth,
                maxWidth = filter?.maxWidth,
                minHeight = filter?.minHeight,
                maxHeight = filter?.maxHeight,
                minCommission = filter?.minCommission,
                maxCommission = filter?.maxCommission,
                page = page,
                size = size
            )

            response.data?.toDomain()
                ?: throw Exception("카페 목록을 불러올 수 없습니다")
        }
    }

    override suspend fun getCafeDetail(cafeId: Long): Result<CafeDetail> {
        return runCatching {
            val response = cafeApiService.getCafeDetail(cafeId)

            response.data?.cafeDetail?.toDomain()
                ?: throw Exception("카페 정보를 불러올 수 없습니다")
        }
    }
}