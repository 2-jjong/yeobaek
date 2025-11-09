package com.ssafy.yeobaeg.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.profile.UserInfo
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ChatUiState(
    val chatRooms: List<ChatRoomSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        loadChatRooms()
    }

    private fun loadChatRooms() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            // 실제 API 연동 시
            // val response = chatRepository.getChatRooms()
            // _uiState.value = ChatUiState(chatRooms = response, isLoading = false)
            // API 연동 전 임시 데이터
            val sampleRooms = listOf(
                ChatRoomSummary(
                    chatRoomId = 1,
                    partnerId = 101,
                    partnerName = "김아티스트",
                    partnerProfileImageUrl = "https://picsum.photos/200/200",
                    partnerRole = UserRole.ARTIST,
                    cafeId = 0,
                    cafeName = "",
                    artworkId = 0,
                    artworkTitle = "",
                    artworkThumbnailUrl = "",
                    unreadCount = 3,
                    lastMessage = "전시 일정 관련해서 문의드립니다.",
                    lastMessageType = "TEXT",
                    lastMessageAt = "5분 전"
                ),
                ChatRoomSummary(
                    chatRoomId = 2,
                    partnerId = 102,
                    partnerName = "카페 여백",
                    partnerProfileImageUrl = "https://picsum.photos/201/201",
                    partnerRole = UserRole.CAFE_OWNER,
                    cafeId = 0,
                    cafeName = "",
                    artworkId = 0,
                    artworkTitle = "",
                    artworkThumbnailUrl = "",
                    unreadCount = 0,
                    lastMessage = "작품 잘 도착했습니다. 감사합니다!",
                    lastMessageType = "TEXT",
                    lastMessageAt = "1시간 전"
                )
            )

            _uiState.value = ChatUiState(chatRooms = sampleRooms, isLoading = false)
        } catch (e: Exception) {
            _uiState.value = ChatUiState(errorMessage = e.message, isLoading = false)
        }
    }


    fun navigateToChatRoom(chatRoomId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ChattingScreen(chatRoomId))
    }
    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}
