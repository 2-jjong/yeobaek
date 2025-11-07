package com.ssafy.yeobaeg.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import com.ssafy.yeobaeg.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoomSummary>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoomSummary>> = _chatRooms

    init {
        loadDummyChatRooms()
    }

    private fun loadDummyChatRooms() = viewModelScope.launch {
        val dummy = listOf(
            ChatRoomSummary(
                chatRoomId = 1,
                partnerId = 101,
                partnerName = "홍길동",
                partnerProfileImageUrl = "https://picsum.photos/200/200",
                partnerRole = UserRole.ARTIST,
                cafeId = 201,
                cafeName = "카페 여백",
                artworkId = 301,
                artworkTitle = "봄의 정원",
                artworkThumbnailUrl = "https://picsum.photos/300/200",
                unreadCount = 2,
                lastMessage = "작품 배송 일정 확인 부탁드려요.",
                lastMessageType = "TEXT",
                lastMessageAt = "1시간 전"
            ),
            ChatRoomSummary(
                chatRoomId = 2,
                partnerId = 102,
                partnerName = "이서연",
                partnerProfileImageUrl = "https://picsum.photos/200/201",
                partnerRole = UserRole.CAFE_OWNER,
                cafeId = 202,
                cafeName = "카페 아뜰리에",
                artworkId = 302,
                artworkTitle = "푸른 바다",
                artworkThumbnailUrl = "https://picsum.photos/300/201",
                unreadCount = 0,
                lastMessage = "좋아요! 일정 확정되면 연락드릴게요.",
                lastMessageType = "TEXT",
                lastMessageAt = "1일 전"
            )
        )
        _chatRooms.value = dummy
    }
}
