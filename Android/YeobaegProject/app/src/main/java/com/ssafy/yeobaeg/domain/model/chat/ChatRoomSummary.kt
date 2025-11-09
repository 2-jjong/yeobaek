package com.ssafy.yeobaeg.domain.model.chat


data class ChatRoomSummary (
    val chatRoomId:Long,
    val partnerId:Long,
    val partnerName:String,
    val partnerProfileImageUrl:String,
    val partnerRole: String,
    val unreadCount:Int,
    val lastMessage:String,
    val lastMessageType:String,
    val lastMessageAt:String
)