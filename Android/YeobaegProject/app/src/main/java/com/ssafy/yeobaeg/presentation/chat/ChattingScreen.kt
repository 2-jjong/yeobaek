package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

data class ChatMessage(
    val id: Int,
    val sender: String, // "me" or "partner"
    val content: String,
    val time: String,
    val profileImage: String? = null
)

@Composable
fun ChattingScreen() {
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
                title = "김아티스트",
                onBackClick = {},
                showBackButton = true,
                actionIcon = null,
                onActionClick = null
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
                border = BorderStroke(color = YeobaegLightGray, width = 0.5.dp)
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
                color = YeobaegBrown
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
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(YeobaegWhite)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text("메시지를 입력하세요...", color = YeobaegTextGray)
            },
            modifier = Modifier
                .weight(1f)
                .height(48.dp),

            )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onSendClick) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "보내기",
                tint = YeobaegBrown
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChattingScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(1, "partner", "안녕하세요! 전시 공간에 대해 문의드리고 싶습니다.", "14:30"),
        ChatMessage(2, "me", "안녕하세요! 어떤 작품을 전시하고 싶으신가요?", "14:32"),
        ChatMessage(3, "partner", "추상화 작품 10점 정도 전시하고 싶습니다. 공간 크기는 어떻게 되나요?", "14:35"),
        ChatMessage(4, "me", "저희 갤러리 공간은 약 50평 정도이고, 벽면 전시 공간이 충분합니다. 언제쯤 전시를 원하시나요?", "14:38"),
        ChatMessage(5, "partner", "다음 달 중순쯤 생각하고 있습니다. 비용은 어떻게 되나요?", "14:40"),
        ChatMessage(6, "me", "전시 기간과 작품 수에 따라 달라지는데, 자세한 내용은 매칭 요청을 통해 논의해보시면 어떨까요?", "14:42")
    )
    ChattingScreen()
}