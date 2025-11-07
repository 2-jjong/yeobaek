package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.model.cafe.CafeFilter
import com.ssafy.yeobaeg.domain.model.cafe.CafeListResult
import com.ssafy.yeobaeg.domain.model.cafe.CafeSummary
import com.ssafy.yeobaeg.domain.model.cafeowner.RegisterCafe

interface CafeRepository {

    suspend fun registerCafe(cafe: RegisterCafe)

    suspend fun getPopularCafes(size: Int = 10): Result<List<CafeSummary>>

    /**
     * 카페 목록 조회
     *
     * @param keyword 카페명 검색 키워드
     * @param latitude 위도
     * @param longitude 경도
     * @param filter 카페 필터 (작가 전용)
     * @param page 페이지 번호
     * @param size 페이지 크기
     */
    suspend fun getCafes(
        keyword: String? = null,
        latitude: Double,
        longitude: Double,
        filter: CafeFilter? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<CafeListResult>

    /**
     * 카페 상세 정보 조회
     *
     * @param cafeId 카페 ID
     */
    suspend fun getCafeDetail(cafeId: Long): Result<CafeDetail>
}