package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.CafeApiService
import com.ssafy.yeobaeg.data.model.cafe.toDomain
import com.ssafy.yeobaeg.data.model.request.toDto
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeListResult
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import javax.inject.Inject

class CafeRepositoryImpl @Inject constructor(
    private val cafeApiService: CafeApiService
) : CafeRepository {

    override suspend fun registerCafe(cafe: RegisterCafe) {
        runCatching { cafeApiService.registerCafe(cafe.toDto()) }
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
}