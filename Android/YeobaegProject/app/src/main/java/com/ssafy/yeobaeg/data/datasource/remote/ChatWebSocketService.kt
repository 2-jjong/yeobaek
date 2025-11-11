package com.ssafy.yeobaeg.data.datasource.remote

import android.annotation.SuppressLint
import android.util.Log
import com.ssafy.yeobaeg.BuildConfig
import com.ssafy.yeobaeg.data.datasource.local.TokenDataSource
import com.ssafy.yeobaeg.data.model.ws.resquest.SendMessageRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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

    private var currentChatRoomId: Long? = null
    private var shouldReconnect = true

    init {
        // Heartbeat 설정: 10초마다 핑 전송
        stompClient.withClientHeartbeat(10000).withServerHeartbeat(10000)
    }
    @SuppressLint("CheckResult")
    suspend fun connectAndSubscribe(chatRoomId: Long) {
        val accessToken = tokenDataSource.getAccessToken() ?: ""
        val headers = listOf(StompHeader("Authorization", "Bearer $accessToken"))

        Log.d(TAG, "🚀 Starting connectAndSubscribe for room: $chatRoomId")

        stompClient.lifecycle().subscribe { lifecycleEvent ->
            Log.d(TAG, "🔄 Lifecycle event received: ${lifecycleEvent.type}")

            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d(TAG, "✅ WebSocket OPENED → Subscribing immediately")
                    subscribeToRoom(chatRoomId)
                }

                LifecycleEvent.Type.CLOSED -> {
                    Log.d(TAG, "❌ WebSocket CLOSED")

                    // 자동 재연결 시도
                    if (shouldReconnect && currentChatRoomId != null) {
                        Log.d(TAG, "🔄 Attempting to reconnect...")
                        GlobalScope.launch {
                            kotlinx.coroutines.delay(2000) // 2초 후 재연결
                            if (shouldReconnect) {
                                stompClient.connect(headers)
                            }
                        }
                    }
                }

                LifecycleEvent.Type.ERROR -> {
                    Log.e(TAG, "⚠️ WebSocket ERROR: ${lifecycleEvent.message}")
                    Log.e(TAG, "⚠️ Exception: ${lifecycleEvent.exception}")
                }

                else -> {
                    Log.d(TAG, "ℹ️ Other lifecycle event: ${lifecycleEvent.type}")
                }
            }
        }

        // 연결 시작
        Log.d(TAG, "🔌 Calling stompClient.connect()")
        stompClient.connect(headers)
    }

    @SuppressLint("CheckResult")
     fun subscribeToRoom(chatRoomId: Long) {
        val destination = "/sub/chat/room/$chatRoomId"
        Log.d(TAG, "📡 Subscribing to: $destination")

        stompClient.topic(destination).subscribe({ topicMessage ->
            Log.d(TAG, "📨 Received message: ${topicMessage.payload}")
            _messageChannel.trySend(topicMessage.payload)
        }, { error ->
            Log.e(TAG, "❌ Subscription error: ${error.message}", error)
        })

        Log.d(TAG, "✅ Subscription request sent for: $destination")
    }

    @SuppressLint("CheckResult")
    fun sendMessage(request: SendMessageRequest) {
        if (!stompClient.isConnected) {
            Log.w(TAG, "⚠️ Cannot send message: WebSocket not connected")
            return
        }
        val jsonBody = Json.encodeToString(request)
        Log.d(TAG, "📤 Sending message: $jsonBody")

        stompClient.send("/pub/messages", jsonBody)
            .subscribe({
                Log.d(TAG, "✅ Message sent successfully")
            }, { error ->
                Log.e(TAG, "❌ Send failed: ${error.message}", error)
            })
    }

    fun disconnect() {
        Log.d(TAG, "🔌 Disconnecting WebSocket")
        stompClient.disconnect()
    }
}