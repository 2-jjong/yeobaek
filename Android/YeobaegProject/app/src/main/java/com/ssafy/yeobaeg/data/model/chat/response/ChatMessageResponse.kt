package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    val messageId: Long,
    val senderId: Long,
    val messageType: String,
    val content: String,
    val imageUrl: String?=null,
    val exhibitionId:Long?=null,
    val exhibition: ExhibitionInfoResponse?=null,
    val isRead: Boolean,
    val createdAt: String
)

fun ChatMessageResponse.toDomain(): ChatMessage {
    return ChatMessage(
        messageId = messageId,
        senderId = senderId,
        messageType = messageType,
        content = content,
        imageUrl = imageUrl,
        exhibitionId=exhibitionId,
        exhibition = exhibition,
        isRead = isRead,
        createdAt = createdAt
    )
}