package com.ssafy.yeobaeg.data.model.chat.response

import com.ssafy.yeobaeg.data.model.PaginationResponse
import com.ssafy.yeobaeg.data.model.toDomain
import com.ssafy.yeobaeg.domain.model.chat.ChatList
import kotlinx.serialization.Serializable

@Serializable
data class ChatListResponse(
    val chatRooms: List<ChatRoomSummaryResponse>,
    val pagination: PaginationResponse
)
fun ChatListResponse.toDomain(): ChatList {
    return ChatList(
        chatRooms = chatRooms.map { it.toDomain() },
        pagination = pagination.toDomain()
    )
}


