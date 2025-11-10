package com.ssafy.yeobaeg.data.datasource.remote

import android.annotation.SuppressLint
import android.util.Log
import com.ssafy.yeobaeg.BuildConfig
import com.ssafy.yeobaeg.data.datasource.local.TokenDataSource
import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import javax.inject.Inject

private const val TAG = "ChatWebSocketService"
class ChatWebSocketService @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val wsUrl: String = BuildConfig.WS_URL

) {
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl)
    private val _messageChannel = Channel<String>(Channel.BUFFERED)
    val messageFlow = _messageChannel.receiveAsFlow()

    private var onConnected: (() -> Unit)? = null

    /**
     * 웹소켓 연결 (성공 시 onConnected 콜백 실행)
     */
    @SuppressLint("CheckResult")
    suspend fun connect(onConnected: (() -> Unit)? = null) {
        val accessToken = tokenDataSource.getAccessToken() ?: ""
        Log.d(TAG, "connect: $wsUrl")
        val headers = listOf(StompHeader("Authorization", "Bearer $accessToken"))

        stompClient.connect(headers)

        stompClient.lifecycle().subscribe {
            when (it.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("WebSocket", "✅ Connected via STOMP")
                    onConnected?.invoke() // 연결 성공 시 콜백 호출
                }
                LifecycleEvent.Type.ERROR ->
                    Log.e("WebSocket", "❌ Error: ${it.exception}")
                LifecycleEvent.Type.CLOSED ->
                    Log.d("WebSocket", "🔌 Disconnected")
                else -> {}
            }
        }
    }


    /**
     * 특정 채팅방 구독
     */
    @SuppressLint("CheckResult")
    fun subscribeToRoom(chatRoomId: Long) {
        val destination = "/sub/chat/$chatRoomId"
        stompClient.topic(destination)
            .subscribe({ topicMessage ->
                Log.d("WebSocket", "📩 Received message: ${topicMessage.payload}")
                _messageChannel.trySend(topicMessage.payload)
            }, { error ->
                Log.e("WebSocket", "❌ Subscription error: ${error.message}", error)
            })
    }

    /**
     * 메시지 전송
     */
    @SuppressLint("CheckResult")
    fun sendMessage(request: SendMessageRequest) {
        if (!stompClient.isConnected) {
            Log.e("WebSocket", "❌ Tried to send but not connected yet!")
            return
        }

        val jsonBody = Json.encodeToString(request)
        stompClient.send("/pub/messages", jsonBody)
            .subscribe(
                { Log.d("WebSocket", "🚀 Sent message: $jsonBody") },
                { error -> Log.e("WebSocket", "❌ Send failed: ${error.message}", error) }
            )
    }

    /**
     * 연결 해제
     */
    fun disconnect() {
        stompClient.disconnect()
        Log.d("WebSocket", "🧹 Disconnected STOMP client")
    }

    /**
     * 연결 + 특정 채팅방 구독 한 번에
     */
    suspend fun connectAndSubscribe(chatRoomId: Long) {
        connect { subscribeToRoom(chatRoomId) }
    }
}
