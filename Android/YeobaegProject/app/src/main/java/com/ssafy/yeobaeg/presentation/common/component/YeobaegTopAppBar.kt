package com.ssafy.yeobaeg.presentation.common.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeobaegTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    showBackButton: Boolean = true
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = YeobaegWhite
        )
    )
}