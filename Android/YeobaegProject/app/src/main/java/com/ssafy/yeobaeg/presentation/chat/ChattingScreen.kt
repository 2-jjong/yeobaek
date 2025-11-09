package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.*
import androidx.compose.material3.TextFieldDefaults
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import kotlinx.coroutines.launch

@Composable
fun ChattingScreen(
    chatRoomId: Long,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatRoomId) {
        viewModel.loadChatMessages(chatRoomId)
        viewModel.connectWebSocket(chatRoomId)
    }

    //  화면 나갈 때 해제
    DisposableEffect(Unit) {
        onDispose { viewModel.disconnectWebSocket() }
    }

    var inputText by remember { mutableStateOf("") }

    ChattingScreenContent(
        title = uiState.chatRoomMessages.chatRoom.partnerName,
        profileImg = uiState.chatRoomMessages.chatRoom.partnerImageUrl,
        messages = uiState.chatRoomMessages.messages,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        inputText = inputText,
        onInputChange = { inputText = it },
        onSendClick = {
            coroutineScope.launch {
                // TODO: 메시지 전송 로직 추가
                inputText = ""
            }
        },
        onBackClick = viewModel::navigateBack
    )
}

/**
 * 실제 UI를 구성하는 함수
 */
@Composable
fun ChattingScreenContent(
    title: String,
    profileImg: String?,
    messages: List<ChatMessage> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    inputText: String = "",
    onInputChange: (String) -> Unit = {},
    onSendClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = title,
                onBackClick = onBackClick ?: {},
                showBackButton = true
            )
        },
        bottomBar = {
            ChatInputBar(
                value = inputText,
                onValueChange = onInputChange,
                onSendClick = onSendClick
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            errorMessage != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage, color = Color.Red)
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(YeobaegWhite)
                    .padding(innerPadding)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.messageId }) { msg ->
                    if (msg.senderType == "ME") MyMessageBubble(msg)
                    else PartnerMessageBubble(msg, profileImg)
                }

            }
        }
    }
}

@Composable
fun PartnerMessageBubble(message: ChatMessage, profileImg: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = profileImg,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .border(0.5.dp, YeobaegLightGray, CircleShape)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = YeobaegWhite,
                border = BorderStroke(color = YeobaegLightGray, width = 0.5.dp),
                modifier = Modifier
                    .widthIn(min = 40.dp, max = 300.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = message.content,
                    fontFamily = jansilFont,
                    color = YeobaegBlack,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun MyMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = YeobaegBrown,
                modifier = Modifier
                    .widthIn(min = 40.dp, max = 300.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = message.content,
                    fontFamily = jansilFont,
                    color = YeobaegWhite,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onAddClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = YeobaegLightGray.copy(alpha = 0.15f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onAddClick() }
            ) {
                Text("+", color = YeobaegTextGray, fontSize = 22.sp)
            }
        }

        Surface(
            modifier = Modifier
                .weight(1f)
                .background(YeobaegWhite),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, YeobaegLightGray),
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = "메시지를 입력하세요...",
                        color = YeobaegTextGray,
                        fontSize = 13.sp,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                textStyle = LocalTextStyle.current.copy(lineHeight = 16.sp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )
            )
        }

        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = YeobaegBrown
        ) {
            IconButton(onClick = onSendClick) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "보내기",
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChattingScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(1, "PARTNER", "TEXT", "안녕하세요", "", null, false, "2시간 전"),
        ChatMessage(2, "ME", "TEXT", "안녕하세요", "", null, false, "2시간 전")
    )
    ChattingScreenContent(
        title = "김아티스트",
        messages = dummyMessages,
        profileImg = ""
    )
}
