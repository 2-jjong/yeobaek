package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.domain.model.chat.ChatRoom
import kotlinx.serialization.Serializable

@Serializable
data class ChatRoomResponse(
    val chatRoomId: Long,
    val partnerId: Long,
    val partnerName: String,
    val partnerImageUrl: String,
    val partnerRole: String
)

fun ChatRoomResponse.toDomain(): ChatRoom {
    return ChatRoom(
        chatRoomId = chatRoomId,
        partnerId = partnerId,
        partnerName = partnerName,
        partnerImageUrl = partnerImageUrl,
        partnerRole = partnerRole,
    )
}