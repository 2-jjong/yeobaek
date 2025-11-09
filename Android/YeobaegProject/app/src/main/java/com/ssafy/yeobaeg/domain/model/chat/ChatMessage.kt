package com.ssafy.yeobaeg.domain.model.chat

import com.ssafy.yeobaeg.data.model.chat.response.ExhibitionInfoResponse

data class ChatMessage(
    val messageId: Long,
    val senderType: String,
    val messageType: String,
    val content: String,
    val imageUrl: String?=null,
    val exhibition: ExhibitionInfoResponse?=null,
    val isRead: Boolean,
    val createdAt: String
)