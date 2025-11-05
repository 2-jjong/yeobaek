@file:OptIn(ExperimentalMaterial3Api::class)

package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray

@Composable
fun SlotDetailSheet(
    visible: Boolean,
    sheetState: SheetState,
    slot: DisplaySlot,
    onDismissRequest: () -> Unit,
    onClickClose: () -> Unit,
    onClickSetting: () -> Unit,
) {
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${slot.wName} 상세 정보",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(YeobaegLightGray.copy(alpha = 0.35f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("크기", color = Color(0xFF7A7F87), fontSize = 12.sp)
                        Text("${slot.width.toInt()}cm × ${slot.height.toInt()}cm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    StatusChip(status = slot.status)
                }
            }

            if (slot.status == SlotStatus.ONGOING || slot.artTitle.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(YeobaegLightBrown.copy(alpha = 0.12f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("현재 전시 중", color = YeobaegBrown, fontWeight = FontWeight.SemiBold)
                    Text(slot.artTitle, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    if (slot.artist.isNotBlank()) Text("작가: ${slot.artist}", color = YeobaegBrown)
                    if (slot.deadline.isNotBlank()) Text("종료일: ${slot.deadline}", color = YeobaegBrown)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                YeobaegOutlinedButton(
                    modifier = Modifier.weight(1f).height(52.dp),
                    text = "닫기",
                    onClick = onClickClose
                )
                YeobaegButton(
                    modifier = Modifier.weight(1f).height(52.dp),
                    text = "슬롯 설정",
                    onClick = onClickSetting
                )
            }
        }
    }
}
