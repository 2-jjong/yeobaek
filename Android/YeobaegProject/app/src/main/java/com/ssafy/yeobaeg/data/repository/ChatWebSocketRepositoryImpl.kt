package com.ssafy.yeobaeg.data.repository

import android.util.Log
import com.ssafy.yeobaeg.data.datasource.remote.ChatWebSocketService
import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import com.ssafy.yeobaeg.domain.model.ws.SendMessage
import com.ssafy.yeobaeg.domain.repository.ChatWebSocketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketRepositoryImpl @Inject constructor(
    private val chatWebSocketService: ChatWebSocketService
) : ChatWebSocketRepository {
    override suspend fun connect() {
        chatWebSocketService.connect()
    }

    override fun subscribeToRoom(chatRoomId: Long) {
        chatWebSocketService.subscribeToRoom(chatRoomId)
    }

    override fun sendMessage(message: SendMessage) {
        val request = SendMessageRequest(
            chatRoomId = message.chatRoomId,
            messageType = message.messageType,
            content = message.content,
            imageUrl = message.imageUrl
        )
        chatWebSocketService.sendMessage(request)
    }

    override fun disconnectWebSocket() {
        chatWebSocketService.disconnect()
    }

    override fun unsubscribeFromRoom(chatRoomId: Long) {
        chatWebSocketService.unsubscribeFromRoom(chatRoomId)
    }

    override fun receiveMessages(): Flow<String> =
        chatWebSocketService.messageFlow
            .onEach { Log.d("WS_REPO", "repo emit = $it") }

}
