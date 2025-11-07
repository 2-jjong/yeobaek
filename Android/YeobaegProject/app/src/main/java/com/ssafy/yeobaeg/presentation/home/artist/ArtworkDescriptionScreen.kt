package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.presentation.home.artist.component.ArtworkSummaryCard
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkDescriptionScreen(
    onRegister: () -> Unit,
    viewModel: ArtworkRegisterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val data = uiState.artworkData

    ArtworkDescriptionContent(
        description = data.description,
        onDescriptionChange = { viewModel.updateArtworkData(description = it) },
        onRegister = onRegister,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun ArtworkDescriptionContent(
    description: String,
    onDescriptionChange: (String) -> Unit,
    onRegister: () -> Unit,
    viewModel: ArtworkRegisterViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp).clearFocusOnTap()) {
        Text(
            text = "작품 설명",
            fontFamily = jansilFont,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 500) onDescriptionChange(it) },
            placeholder = {
                Text(
                    text = "작품의 컨셉, 제작 배경, 의미 등을 자유롭게 작성해주세요.",
                    color = YeobaegTextGray,
                    fontFamily = jansilFont,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier.fillMaxWidth().height(150.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${description.length}/500",
            fontFamily = jansilFont,
            fontSize = 12.sp,
            color = YeobaegTextGray
        )

        Spacer(Modifier.height(24.dp))
        ArtworkSummaryCard(viewModel)
        Spacer(Modifier.weight(1f))

        YeobaegButton(
            text = "작품 등록",
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = description.isNotBlank()
        )
    }
}
