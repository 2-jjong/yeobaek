package com.ssafy.yeobaeg.presentation.common.component

import android.R.attr.contentDescription
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    @DrawableRes actionIcon: Int? = null,
    iconTint: Color = YeobaegBrown,
    onActionClick: (() -> Unit)? = null
) {
    androidx.compose.foundation.layout.Column {
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
            actions = {
                if (onActionClick != null && actionIcon != null) {
                    IconButton(onClick = onActionClick) {
                        Icon(
                            painter = painterResource(actionIcon),
                            contentDescription = "아이콘",
                            tint = iconTint,
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = YeobaegWhite
            ),
            windowInsets = WindowInsets(0.dp)
        )

        HorizontalDivider(
            color = Color(0xFFE0E0E0),
            thickness = 0.5.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    YeobaegTopAppBar("프로필", {}, true)
}
