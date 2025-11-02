package com.ssafy.yeobaeg.presentation.common.component

import android.R.attr.contentDescription
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.presentation.profile.ProfileEditScreen
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeobaegTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    showBackButton: Boolean = true,
    @DrawableRes actionIcon: Int?=null,
    onActionClick:(()->Unit)?=null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Medium,
                color = YeobaegBlack
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = YeobaegBlack
                    )
                }
            }
        },

        actions={
            if(onActionClick!=null &&actionIcon!=null){
                IconButton(onClick = onActionClick) {
                    Icon(
                        painter = painterResource(actionIcon),
                        contentDescription = "아이콘",
                        tint= YeobaegBrown,
                   )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = YeobaegWhite
        ),
        // Compose의 TopAppBar에 기본으로 있는 상단 여백 없애기
        windowInsets = WindowInsets(0.dp)

    )
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    YeobaegTopAppBar("test",{},true)
}