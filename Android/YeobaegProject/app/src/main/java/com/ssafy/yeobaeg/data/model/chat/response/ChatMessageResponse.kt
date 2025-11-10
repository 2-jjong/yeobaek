package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    val messageId: Long,
    val senderType: String,
    val messageType: String,
    val content: String,
    val imageUrl: String?=null,
    val exhibition: ExhibitionInfoResponse?=null,
    val isRead: Boolean,
    val createdAt: String
)

fun ChatMessageResponse.toDomain(): ChatMessage {
    return ChatMessage(
        messageId = messageId,
        senderType = senderType,
        messageType = messageType,
        content = content,
        imageUrl = imageUrl,
        exhibition = exhibition,
        isRead = isRead,
        createdAt = createdAt
    )
}