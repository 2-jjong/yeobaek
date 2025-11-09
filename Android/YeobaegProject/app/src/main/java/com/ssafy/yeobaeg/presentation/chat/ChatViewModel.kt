package com.ssafy.yeobaeg.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.repository.ChatRepository
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
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    init {
        loadChatRooms()
    }

    private fun loadChatRooms() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        chatRepository.getChatList()
            .onSuccess { chatList ->
                _uiState.value = ChatUiState(
                    chatRooms = chatList.chatRooms,
                    isLoading = false
                )
            }
            .onFailure { e ->
                _uiState.value = ChatUiState(
                    errorMessage = e.message ?: "채팅 목록을 불러오는 중 오류가 발생했습니다.",
                    isLoading = false
                )
            }
    }


    fun navigateToChatRoom(chatRoomId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ChattingScreen(chatRoomId))
    }
    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}
