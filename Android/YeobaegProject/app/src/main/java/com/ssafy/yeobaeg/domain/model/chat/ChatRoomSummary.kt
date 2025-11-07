package com.ssafy.yeobaeg.domain.model.chat

import com.ssafy.yeobaeg.domain.model.UserRole

data class ChatRoomSummary (
    val chatRoomId:Long,
    val partnerId:Long,
    val partnerName:String,
    val partnerProfileImageUrl:String,
    val partnerRole: UserRole,
    val cafeId:Long,
    val cafeName:String,
    val artworkId:Long,
    val artworkTitle:String,
    val artworkThumbnailUrl:String,
    val unreadCount:Int,
    val lastMessage:String,
    val lastMessageType:String,
    val lastMessageAt:String
)