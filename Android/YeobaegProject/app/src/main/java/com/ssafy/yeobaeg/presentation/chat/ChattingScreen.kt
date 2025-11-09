package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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

data class ChatMessage(
    val id: Int,
    val sender: String, // "me" or "partner"
    val content: String,
    val time: String,
    val profileImage: String? = null
)

@Composable
fun ChattingScreen(
    chatRoomId: Long,
    viewModel: ChatViewModel= hiltViewModel()
) {
    // 실제 화면에선 viewModel 데이터 사용
    ChattingScreenContent(
        title = "김아티스트",
        onBackClick = viewModel::navigateBack
    )
}

/**
 * Preview 전용 또는 ViewModel 없는 UI만 분리
 */
@Composable
fun ChattingScreenContent(
    title: String,
    onBackClick: (() -> Unit)? = null
) {
    val dummyMessages = listOf(
        ChatMessage(1, "partner", "안녕하세요! 전시 공간에 대해 문의드리고 싶습니다.", "14:30"),
        ChatMessage(2, "me", "안녕하세요! 어떤 작품을 전시하고 싶으신가요?", "14:32"),
        ChatMessage(3, "partner", "추상화 작품 10점 정도 전시하고 싶습니다. 공간 크기는 어떻게 되나요?", "14:35"),
        ChatMessage(4, "me", "저희 갤러리 공간은 약 50평 정도이고, 벽면 전시 공간이 충분합니다. 언제쯤 전시를 원하시나요?", "14:38"),
        ChatMessage(5, "partner", "다음 달 중순쯤 생각하고 있습니다. 비용은 어떻게 되나요?", "14:40"),
        ChatMessage(6, "me", "전시 기간과 작품 수에 따라 달라지는데, 자세한 내용은 매칭 요청을 통해 논의해보시면 어떨까요?", "14:42")
    )

    var inputText by remember { mutableStateOf("") }

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
                onValueChange = { inputText = it },
                onSendClick = { /* TODO */ }
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite)
                .padding(innerPadding)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dummyMessages) { msg ->
                if (msg.sender == "me") MyMessageBubble(msg)
                else PartnerMessageBubble(msg)
            }
        }
    }
}

@Composable
fun PartnerMessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        AsyncImage(
            model = message.profileImage ?: "",
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
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
                text = message.time,
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
                text = message.time,
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
        // 왼쪽 + 버튼
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = YeobaegLightGray.copy(alpha = 0.15f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { }
            ) {
                Text("+", color = YeobaegTextGray, fontSize = 22.sp)
            }
        }

        // 메시지 입력창
        Surface(
            modifier = Modifier.weight(1f).background(YeobaegWhite),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, YeobaegLightGray),
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text="메시지를 입력하세요...",
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

        // 전송 버튼
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = YeobaegLightGray.copy(alpha = 0.5f)
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
    ChattingScreenContent(title = "김아티스트")
}
