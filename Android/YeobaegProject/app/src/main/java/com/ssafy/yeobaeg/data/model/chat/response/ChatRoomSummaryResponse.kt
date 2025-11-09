package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomSummaryResponse(
    val chatRoomId: Long,
    val partnerId: Long,
    val partnerName: String,
    val partnerProfileImageUrl: String,
    val partnerRole: String,
    val unreadCount: Int,
    val lastMessage: String,
    val lastMessageType: String,
    val lastMessageAt: String
)

fun ChatRoomSummaryResponse.toDomain(): ChatRoomSummary {
    return ChatRoomSummary(
        chatRoomId = chatRoomId,
        partnerId = partnerId,
        partnerName = partnerName,
        partnerProfileImageUrl = partnerProfileImageUrl,
        partnerRole = partnerRole,
        unreadCount = unreadCount,
        lastMessage = lastMessage,
        lastMessageType = lastMessageType,
        lastMessageAt = lastMessageAt
    )
}