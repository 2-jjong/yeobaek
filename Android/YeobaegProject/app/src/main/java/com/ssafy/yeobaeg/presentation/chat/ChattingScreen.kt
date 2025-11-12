package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.chat.ChatMessage
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.common.util.DateFormatter
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingScreen(
    chatRoomId: Long,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val myUserId = viewModel.myUserId.collectAsState().value

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
        onSendClick = { message ->
            if (message.isNotBlank()) {
                viewModel.sendMessage(message, myUserId)
                inputText = ""
            }
        },
        onBackClick = viewModel::navigateBack,
        myUserId = myUserId
    )
}

/**
 * 실제 UI를 구성하는 함수
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChattingScreenContent(
    title: String,
    profileImg: String?,
    messages: List<ChatMessage> = emptyList(),
    isLoading: Boolean = false,
    errorMessage: String? = null,
    inputText: String = "",
    onInputChange: (String) -> Unit = {},
    onSendClick: (String) -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    myUserId: Long?
) {
    val listState = rememberLazyListState()

    // 키보드가 올라올 때와 새 메시지가 추가될 때 자동 스크롤
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            // reverseLayout이므로 0번 인덱스가 최신 메시지
            delay(100) // 키보드 애니메이션 대기
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
//            .imePadding(), // 전체 Scaffold에 IME padding 적용
        topBar = {
            YeobaegTopAppBar(
                title = title,
                onBackClick = onBackClick ?: {},
                showBackButton = true
            )
        },
        bottomBar = {
            ChatInputBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YeobaegWhite)
                    .imePadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                value = inputText,
                onValueChange = onInputChange,
                onSendClick = onSendClick
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),

        ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegBackground)
                .padding(innerPadding)
        ) {
            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                errorMessage != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage, color = Color.Red)
                }

                else -> LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clearFocusOnTap()
                        .background(YeobaegWhite)
                        .padding(horizontal = 12.dp),
                    reverseLayout = true, // 최신 메시지가 아래에 표시되도록
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // reverseLayout이므로 역순으로 표시
                    items(
                        items = messages.asReversed(),
                        key = { "${it.messageId}_${it.createdAt}" }
                    ) { msg ->
                        val isMyMessage = myUserId != null && msg.senderId == myUserId
                        if (isMyMessage) {
                            MyMessageBubble(msg)
                        } else {
                            PartnerMessageBubble(msg, profileImg)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerMessageBubble(message: ChatMessage, profileImg: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = profileImg,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .border(0.5.dp, YeobaegLightGray, CircleShape)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 12.dp
                ),
                color = YeobaegWhite,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .widthIn(min = 40.dp, max = 260.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = message.content,
                    fontFamily = jansilFont,
                    color = YeobaegBlack,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
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
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 4.dp
                ),
                color = YeobaegBrown,
                modifier = Modifier
                    .widthIn(min = 40.dp, max = 260.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = message.content,
                    fontFamily = jansilFont,
                    color = YeobaegWhite,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
            Text(
                text = DateFormatter.toTimeAgo(message.createdAt),
                color = YeobaegTextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.padding(top = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: (String) -> Unit,
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val trimmedValue = value.trim()
    val isSendEnabled = trimmedValue.isNotEmpty()

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically ,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // + 버튼
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = YeobaegLightGray.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable { onAddClick() }
                ) {
                    Text(
                        text = "+",
                        color = YeobaegTextGray,
                        fontSize = 24.sp
                    )
                }
            }

            // 입력 필드
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, YeobaegLightGray.copy(alpha = 0.5f)),
                color = YeobaegWhite
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            text = "메시지를 입력하세요",
                            color = YeobaegTextGray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 42.dp, max = 120.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    ),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (isSendEnabled) {
                                onSendClick(trimmedValue)
                            }
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = YeobaegBrown
                    )
                )
            }

            // 전송 버튼
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = if (isSendEnabled) YeobaegBrown else YeobaegLightGray.copy(alpha = 0.3f)
            ) {
                IconButton(
                    onClick = {
                        if (isSendEnabled) {
                            onSendClick(trimmedValue)
                        }
                    },
                    enabled = isSendEnabled,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "보내기",
                        tint = if (isSendEnabled) Color.White else YeobaegTextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChattingScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(1, 1, "TEXT", "안녕하세요! 작품이 정말 마음에 들어요.", "", null, false, "2시간 전"),
        ChatMessage(2, 0, "TEXT", "감사합니다 😊", "", null, false, "2시간 전"),
        ChatMessage(3, 1, "TEXT", "전시 일정이 어떻게 되나요?", "", null, false, "1시간 전"),
        ChatMessage(4, 0, "TEXT", "다음 주 월요일부터 가능합니다", "", null, false, "1시간 전")
    )
    ChattingScreenContent(
        title = "김아티스트",
        messages = dummyMessages,
        profileImg = "",
        myUserId = 0
    )
}
