package com.ssafy.yeobaeg.data.repository

import com.ssafy.yeobaeg.data.datasource.remote.ChatWebSocketService
import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import com.ssafy.yeobaeg.domain.repository.ChatWebSocketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatWebSocketRepositoryImpl @Inject constructor(
    private val chatWebSocketService: ChatWebSocketService
): ChatWebSocketRepository {
    override suspend fun connectAndSubscribe(chatRoomId: Long) {
        chatWebSocketService.connect {
            chatWebSocketService.subscribeToRoom(chatRoomId)
        }
    }

    override fun sendMessage(request: SendMessageRequest) {
        chatWebSocketService.sendMessage(request)
    }

    override fun disconnectWebSocket() {
        chatWebSocketService.disconnect()
    }

    override fun receiveMessages(): Flow<String> {
        return chatWebSocketService.messageFlow
    }


    override fun subscribeToRoom(chatRoomId: Long) {
        chatWebSocketService.subscribeToRoom(chatRoomId)
    }
}