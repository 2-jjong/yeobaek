package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ChatApiService
import com.ssafy.yeobaeg.data.model.chat.response.toDomain
import com.ssafy.yeobaeg.domain.model.chat.ChatList
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService,
) : ChatRepository {
    override suspend fun getChatList(
        sortBy: String,
        page: Int,
        size: Int
    ): Result<ChatList> {
        return runCatching {
            val response = chatApiService.getChatList(
                sortBy = sortBy,
                page = page,
                size = size
            )
            response.data?.toDomain()
                ?: throw IllegalStateException("Chat list response data is null")
        }
    }
}