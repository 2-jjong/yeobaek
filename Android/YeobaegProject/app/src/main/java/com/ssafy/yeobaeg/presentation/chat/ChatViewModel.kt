package com.ssafy.yeobaeg.presentation.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomInfo
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomMessages
import com.ssafy.yeobaeg.domain.model.chat.MessageType
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionInfo
import com.ssafy.yeobaeg.domain.model.ws.SendMessage
import com.ssafy.yeobaeg.domain.repository.ChatRepository
import com.ssafy.yeobaeg.domain.repository.ChatWebSocketRepository
import com.ssafy.yeobaeg.domain.repository.ExhibitionRepository
import com.ssafy.yeobaeg.domain.repository.StorageRepository
import com.ssafy.yeobaeg.domain.session.AuthSessionManager
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import com.ssafy.yeobaeg.presentation.common.util.uriToTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ChatUiState(
    val chatRooms: List<ChatRoomInfo> = emptyList(),
    val isLoading: Boolean = false,
    val chatRoomMessages: ChatRoomMessages = ChatRoomMessages(),
    val exhibitionInfo: ExhibitionInfo = ExhibitionInfo(),
    val errorMessage: String? = null,
)

private const val TAG = "ChatViewModel"
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val navigator: Navigator,
    private val chatRepository: ChatRepository,
    private val chatWebSocketRepository: ChatWebSocketRepository,
    private val exhibitionRepository: ExhibitionRepository,
    private val storageRepository: StorageRepository,
    private val authSessionManager: AuthSessionManager

) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState
    private var receiveJob: Job? = null
    val myUserId: StateFlow<Long?> = authSessionManager.currentUserId
    val myUserRole: StateFlow<UserRole?> = authSessionManager.currentUserRole

    /** 채팅방 구독 시작 (입장 시) */
    fun subscribeToChatRoom(chatRoomId: Long) {
        viewModelScope.launch {
            delay(500)
            chatWebSocketRepository.subscribeToRoom(chatRoomId)
            Log.d(TAG, "subscribeToChatRoom: 메시지 수신 시작")
            startReceivingMessages()
        }
    }


    /**채팅방 구독 해제 (퇴장 시) */
    fun unsubscribeFromChatRoom(chatRoomId: Long) {
        chatWebSocketRepository.unsubscribeFromRoom(chatRoomId)
        stopReceivingMessages()
    }

    /** 서버로부터 새 메시지 수신 */
    private fun startReceivingMessages() {
        if (receiveJob?.isActive == true) {
            return
        }
        receiveJob = viewModelScope.launch {
            chatWebSocketRepository.receiveMessages().collect { msgJson ->

                val newMessage = parseChatMessage(msgJson)
                if (newMessage.senderId != myUserId.value) {
                    Log.d(TAG, "startReceivingMessages: 바로 업데이트")
                    _uiState.update { state ->
                        state.copy(
                            chatRoomMessages = state.chatRoomMessages.copy(
                                messages = listOf(newMessage)+state.chatRoomMessages.messages
                            )
                        )
                    }
                }
            }
        }
    }

    private fun stopReceivingMessages() {
        receiveJob?.cancel()
        receiveJob = null
    }

    /** 메시지 JSON 파싱 */

    private fun parseChatMessage(json: String): ChatMessage {
        return try {
            Json.decodeFromString(ChatMessage.serializer(), json)
        } catch (e: Exception) {
            ChatMessage(0, 0L, MessageType.TEXT, "파싱 오류", "", 1, null, false, "")
        }
    }

    /** 메시지 전송 */
    fun sendMessage(text: String, myUserId: Long?) {
        val chatRoomId = _uiState.value.chatRoomMessages.chatRoom.chatRoomId
        val message = SendMessage(chatRoomId, "TEXT", text)
        chatWebSocketRepository.sendMessage(message)

        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val nowString = formatter.format(Date())
        val myMsg = ChatMessage(
            messageId = System.currentTimeMillis(),
            senderId = myUserId,
            messageType = MessageType.TEXT,
            content = text,
            imageUrl = "",
            exhibition = null,
            isRead = true,
            createdAt = nowString
        )

        _uiState.update { state ->
            val updatedList = listOf(myMsg) + state.chatRoomMessages.messages
            state.copy(chatRoomMessages = state.chatRoomMessages.copy(messages = updatedList))
        }
    }

    /** 채팅방 목록 불러오기 */
    fun loadChatRooms() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        chatRepository.getChatList()
            .onSuccess { chatList ->
                _uiState.update {
                    it.copy(chatRooms = chatList.chatRooms, isLoading = false, errorMessage = null)
                }
            }
            .onFailure { e ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "채팅 목록 불러오기 오류")
                }
            }
    }

    /** 채팅방 메시지 불러오기 */
    fun loadChatMessages(chatRoomId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        chatRepository.getChatRoomMessages(chatRoomId)
            .onSuccess { result ->
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
                        errorMessage = e.message ?: "메시지 불러오기 오류"
                    )
                }
            }
    }

    /** 전시 수락 */
    fun agreeExhibition(exhibitionId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        exhibitionRepository.agreeExhibitionPropose(exhibitionId)
        _uiState.update { it.copy(isLoading = false) }
    }

    /** 전시 거절 */
    fun rejectExhibition(exhibitionId: Long) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        exhibitionRepository.rejectExhibition(exhibitionId)
        _uiState.update { it.copy(isLoading = false) }
    }


    fun loadExhibitionInfo(exhibitionId: Long) = viewModelScope.launch {
        exhibitionRepository.getExhibitionInfo(exhibitionId)
            .onSuccess { info ->
                _uiState.update { state ->
                    state.copy(exhibitionInfo = info)
                }
            }
            .onFailure { e ->
                _uiState.update { state ->
                    state.copy(errorMessage = e.message ?: "전시 정보 조회 실패")
                }
            }
    }

    fun sendImageMessage(chatRoomId: Long, uri: Uri, context: Context) {
        viewModelScope.launch {
            try {

                // 1) Uri → File 변환
                val file = uriToTempFile(context, uri)

                // 2) Presigned URL 요청 (업로드 개수 = 1)
                val presignedList = storageRepository.createPresignedUrl("IMAGES", 1)
                    .getOrThrow()
                val presigned = presignedList.first()

                // 3) Presigned PUT으로 S3 업로드
                storageRepository.uploadWithPresignedPut(file, presigned.uploadUrl)
                    .getOrThrow()

                val fileKey = presigned.fileKey

                // 4) WebSocket으로 이미지 메시지 전송
                val wsMessage = SendMessage(
                    chatRoomId = chatRoomId,
                    messageType = "IMAGE",
                    content = "",
                    imageUrl = fileKey
                )
                chatWebSocketRepository.sendMessage(wsMessage)

                // 5) UI 즉시 반영
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val now = formatter.format(Date())

                val myMsg = ChatMessage(
                    messageId = System.currentTimeMillis(),
                    senderId = myUserId.value,
                    messageType = MessageType.IMAGE,
                    content = null,
                    imageUrl = presigned.downloadUrl ?: fileKey,
                    exhibition = null,
                    isRead = true,
                    createdAt = now
                )

                _uiState.update { state ->
                    state.copy(
                        chatRoomMessages = state.chatRoomMessages.copy(
                            messages = listOf(myMsg) + state.chatRoomMessages.messages
                        )
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "이미지 전송 오류: ${e.message}")
                }
            }
        }
    }



    fun navigateToChatRoom(chatRoomId: Long) = viewModelScope.launch {
        navigator.navigate(FullScreen.ChattingScreen(chatRoomId))
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

    fun navigateToMatching(partnerId: Long) = viewModelScope.launch {
        navigator.navigate(
            FullScreen.MatchingDateScreen(
                myUserId.value,
                myUserRole.value,
                partnerId
            )
        )
    }
}
