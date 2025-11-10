package com.ssafy.yeobaeg.presentation.arcore.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField

@Composable
fun WallDetailSheet(
    modifier: Modifier = Modifier,
    wall: DisplaySlot,
    onNameChange: (String) -> Unit,
    onMeasureClick: () -> Unit,
    onNextClick: () -> Unit
) {
    var name by remember(wall.wid) { mutableStateOf(wall.wName) }

    Column(modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Text(
            text = "벽 상세 정보",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))

        // 길이
        LabeledValueRow(label = "길이", value = wall.widthText)

        // 높이
        LabeledValueRow(
            label = "높이",
            value = if (wall.height > 0) wall.heightText else "미측정"
        )

        // 이름
        Spacer(Modifier.height(8.dp))
        YeobaegTextField(
            value = name,
            onValueChange = {
                name = it.take(10)
                onNameChange(it.take(10))
            },
            label = "벽 이름",
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))

        // AR 측정 버튼
        YeobaegButton(
            text = "AR로 높이 측정",
            onClick = onMeasureClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(Modifier.height(12.dp))

        // 다음 벽
        YeobaegOutlinedButton(
            text = "다음 벽",
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

/** Label + value 한 줄 */
@Composable
private fun LabeledValueRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Normal)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

