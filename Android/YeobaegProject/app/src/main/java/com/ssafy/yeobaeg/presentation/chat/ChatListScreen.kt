package com.ssafy.yeobaeg.presentation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.UserRole
import com.ssafy.yeobaeg.domain.model.chat.ChatRoomSummary
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ChatListScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onChatClick: (Long) -> Unit = {}
) {
    val chatRooms by viewModel.chatRooms.collectAsState()

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "채팅",
                onBackClick = {},
                showBackButton = false,
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
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            itemsIndexed(chatRooms) { index, chat ->
                ChatListItem(chat = chat, onClick = { onChatClick(chat.chatRoomId) })

                // 마지막 아이템이 아닐 때만 Divider 추가
                if (index < chatRooms.lastIndex) {
                    HorizontalDivider(
                        color = YeobaegLightGray,
                        thickness = 0.7.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatRoomSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(YeobaegWhite)
            .padding(horizontal = 15.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chat.partnerProfileImageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 왼쪽: 이름 + 메시지
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = chat.partnerName,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Medium,
                color = YeobaegBlack,
                fontSize = 17.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = chat.lastMessage,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Light,
                color = YeobaegBlack,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        // 오른쪽: 시간 (위) + 안읽은 수 (아래)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(45.dp) // 두 줄 높이 확보
        ) {
            Text(
                text = chat.lastMessageAt,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Light,
                color = YeobaegTextGray,
                fontSize = 13.sp
            )

            if (chat.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(YeobaegBrown, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.unreadCount.toString(),
                        color = YeobaegWhite,
                        fontFamily = jansilFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(22.dp)) // 빈칸 맞추기용
            }
        }
    }
}


@Composable
fun ChatListPreviewContent(chatRooms: List<ChatRoomSummary>, onChatClick: (Long) -> Unit = {}) {
    LazyColumn {

        itemsIndexed(chatRooms) { index, chat ->
            ChatListItem(chat = chat, onClick = { onChatClick(chat.chatRoomId) })

            // 마지막 아이템이 아닐 때만 Divider 추가
            if (index < chatRooms.lastIndex) {
                HorizontalDivider(
                    color = YeobaegLightGray,
                    thickness = 0.7.dp,
                    modifier = Modifier.padding(horizontal = 8.dp)

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    val dummyChatRooms = listOf(
        ChatRoomSummary(
            chatRoomId = 1,
            partnerId = 101,
            partnerName = "김아티스트",
            partnerProfileImageUrl = "",
            partnerRole = UserRole.ARTIST,
            cafeId = 0,
            cafeName = "",
            artworkId = 0,
            artworkTitle = "",
            artworkThumbnailUrl = "",
            unreadCount = 2,
            lastMessage = "작품 전시에 대해 논의하고 싶습니다",
            lastMessageType = "TEXT",
            lastMessageAt = "2분 전"
        ),
        ChatRoomSummary(
            chatRoomId = 2,
            partnerId = 102,
            partnerName = "Brew & Canvas",
            partnerProfileImageUrl = "",
            partnerRole = UserRole.CAFE_OWNER,
            cafeId = 0,
            cafeName = "",
            artworkId = 0,
            artworkTitle = "",
            artworkThumbnailUrl = "",
            unreadCount = 0,
            lastMessage = "전시 공간이 준비되었습니다",
            lastMessageType = "TEXT",
            lastMessageAt = "1시간 전"
        )
    )

    ChatListPreviewContent(chatRooms = dummyChatRooms)
}
