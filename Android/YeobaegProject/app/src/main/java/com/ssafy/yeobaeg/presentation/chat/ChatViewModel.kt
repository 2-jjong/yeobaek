package com.ssafy.yeobaeg.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.chat.ChatList
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomInfo
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomMessages
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class ChatUiState(
    val chatRooms: List<ChatRoomInfo> = emptyList(),
    val isLoading: Boolean = false,
    val chatRoomMessages: ChatRoomMessages = ChatRoomMessages(),
    val errorMessage: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navigator: Navigator,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun loadChatRooms() = viewModelScope.launch {
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

    fun createChatRoom(partnerId: Long) = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)

        chatRepository.createChatRoom(partnerId)
            .onSuccess { chatRoom ->
                // 생성 성공 시: 새 채팅방으로 바로 이동
                _uiState.value = _uiState.value.copy(isLoading = false)
                navigateToChatRoom(chatRoom.chatRoomId)
            }
            .onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "채팅방 생성 중 오류가 발생했습니다."
                )
            }
    }

    fun loadChatMessages(chatRoomId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        chatRepository.getChatRoomMessages(chatRoomId)
            .onSuccess { result ->
                Log.d("ChatViewModel", "불러온 메시지 개수: ${result.messages.size}")
                Log.d("ChatViewModel", "채팅방 이름: ${result.chatRoom.partnerName}")

                _uiState.update { current ->
                    current.copy(
                        chatRoomMessages = result.copy(
                            messages = result.messages.toList(),
                            chatRoom = result.chatRoom
                        ),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .onFailure { e ->
                _uiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "메시지를 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
    }


    fun navigateToChatRoom(chatRoomId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ChattingScreen(chatRoomId))
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }
}
