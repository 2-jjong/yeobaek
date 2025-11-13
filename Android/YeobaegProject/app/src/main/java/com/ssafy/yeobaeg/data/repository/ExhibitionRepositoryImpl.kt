package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ExhibitionApiService
import com.ssafy.yeobaeg.data.model.exhibition.toDomain
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.repository.ExhibitionRepository
import javax.inject.Inject

class ExhibitionRepositoryImpl @Inject constructor(
    private val exhibitionApiService: ExhibitionApiService
) : ExhibitionRepository {

    override suspend fun getSlotSchedule(
        slotId: Long,
        from: String
    ): Result<List<ExhibitionSchedule>> {
        return runCatching {
            val response = exhibitionApiService.getSlotSchedule(slotId, from)
            response.data?.map { it.toDomain() } ?: emptyList()
        }
    }
}
