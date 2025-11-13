package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.exhibition.ExhibitionScheduleDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExhibitionApiService {

    /**
     * 슬롯의 전시 일정 조회
     *
     * @param slotId 슬롯 ID
     * @param from 시작 날짜 (yyyy-MM-dd 형식, 기본값: 오늘)
     * @return 전시 일정 목록 (30일간)
     */
    @GET("exhibitions/slots/{slotId}")
    suspend fun getSlotSchedule(
        @Path("slotId") slotId: Long,
        @Query("from") from: String
    ): BaseResponse<List<ExhibitionScheduleDto>>
}
