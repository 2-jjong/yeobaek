package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBlue
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGreen
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkYellow
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBlue
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGreen
import com.ssafy.yeobaeg.ui.theme.YeobaegLightYellow

/** 상태 칩 */
@Composable
fun StatusChip(status: SlotStatus) {
    val (bg, fg, label) = when (status) {
        SlotStatus.AVAILABLE -> Triple(YeobaegLightGreen, YeobaegDarkGreen, "전시 가능")
        SlotStatus.ONGOING -> Triple(YeobaegLightBlue, YeobaegDarkBlue, "전시 중")
        SlotStatus.RESERVED -> Triple(YeobaegLightYellow, YeobaegDarkYellow, "예약됨")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}


/**
 * 전시 리스트 아이템
 *
 * @param thumbnail 썸네일 이미지(리소스/URL 등 Coil Any)
 * @param title     슬롯명(예: "입구 좌측 벽")
 * @param status    상태(AVAILABLE/ONGOING/RESERVED)
 * @param workTitle 작품명(전시 중일 때)
 * @param artist    작가명(전시 중일 때)
 * @param endDate   종료 날짜 텍스트(전시 중일 때, 예: "종료: 2024-02-15")
 * @param sizeText  크기 텍스트(전시 가능일 때, 예: "120cm × 80cm")
 * @param onMatchClick 전시 가능일 때 "작품 매칭" 버튼 클릭
 */
@Composable
fun ExhibitionListItem(
    modifier: Modifier = Modifier,
    thumbnail: Any?,
    title: String,
    status: SlotStatus,
    workTitle: String,
    artist: String,
    endDate: String,
    sizeText: String,
    onMatchClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE6E6E8))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 썸네일
            AsyncImage(
                model = thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(YeobaegLightGray)
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                // 상단: 제목 + 상태칩
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.weight(1f))
                    StatusChip(status)
                }
                Spacer(Modifier.height(6.dp))

                when (status) {
                    SlotStatus.ONGOING -> {
                        // 작품명 / 작가 / 종료
                        Text("🎨 $workTitle", fontSize = 13.sp, color = YeobaegDarkGray)
                        Text("👩‍🎨 $artist", fontSize = 13.sp, color = YeobaegDarkGray)
                        Spacer(Modifier.height(2.dp))
                        Text("종료 : $endDate", fontSize = 12.sp, color = YeobaegGray)
                    }

                    SlotStatus.AVAILABLE -> {
                        // 크기 + 작품 매칭 버튼
                        Text("📏 $sizeText", fontSize = 13.sp, color = YeobaegDarkGray)
                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = { onMatchClick() },
                            colors = ButtonDefaults.buttonColors(containerColor = YeobaegDarkBrown),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "작품 매칭",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    SlotStatus.RESERVED -> {
                        // 예약됨: 추가 본문 없이 상태칩/배경으로만 구분
                        Text("🎨 $workTitle", fontSize = 13.sp, color = YeobaegDarkGray)
                        Text("👩‍🎨 $artist", fontSize = 13.sp, color = YeobaegDarkGray)
                        Spacer(Modifier.height(2.dp))
                        Text("종료 : $endDate", fontSize = 12.sp, color = YeobaegGray)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionListItem_Available() {
    MaterialTheme {
        ExhibitionListItem(
            thumbnail = null,
            title = "입구 좌측 벽",
            status = SlotStatus.AVAILABLE,
            sizeText = "120cm × 80cm",
            workTitle = "",
            artist = "",
            endDate = "",
            onMatchClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionListItem_Ongoing() {
    MaterialTheme {
        ExhibitionListItem(
            thumbnail = null,
            title = "메인 전시 공간",
            status = SlotStatus.ONGOING,
            workTitle = "푸른 밤의 꿈",
            artist = "김예술",
            endDate = "2024-12-31",
            sizeText = "",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewExhibitionListItem_Reserved() {
    MaterialTheme {
        ExhibitionListItem(
            thumbnail = null,
            title = "창가 벽면",
            status = SlotStatus.RESERVED,
            sizeText = "100cm × 100cm",
            endDate = "2025-01-15",
            workTitle = "푸른 밤의 꿈",
            artist = "김예술",
        )
    }
}