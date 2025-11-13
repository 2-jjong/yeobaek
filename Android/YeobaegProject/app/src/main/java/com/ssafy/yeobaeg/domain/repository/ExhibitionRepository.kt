package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule

interface ExhibitionRepository {

    /**
     * 슬롯의 전시 일정 조회
     *
     * @param slotId 슬롯 ID
     * @param from 시작 날짜 (yyyy-MM-dd 형식)
     * @return 전시 일정 목록 (30일간)
     */
    suspend fun getSlotSchedule(
        slotId: Long,
        from: String
    ): Result<List<ExhibitionSchedule>>
}
