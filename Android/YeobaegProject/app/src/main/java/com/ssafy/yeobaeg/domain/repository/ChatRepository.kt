package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.chat.ChatList
import com.ssafy.yeobaeg.domain.model.storage.PresignedUrl
import java.io.File

interface ChatRepository {

    suspend fun getChatList(
        sortBy: String = "LATEST",
        page: Int = 0,
        size: Int = 20
    ): Result<ChatList>

}
