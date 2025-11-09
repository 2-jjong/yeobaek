package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import kotlinx.coroutines.flow.Flow

interface ChatWebSocketRepository {
    suspend fun connectAndSubscribe(chatRoomId: Long)
    fun subscribeToRoom(chatRoomId: Long)
    fun sendMessage(request: SendMessageRequest)
    fun disconnectWebSocket()
    fun receiveMessages(): Flow<String>
}