package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkDetailInfoScreen(
    onNext: () -> Unit,
    viewModel: ArtworkRegisterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val data = uiState.artworkData

    ArtworkDetailInfoContent(
        width = if (data.widthCm == 0) "" else data.widthCm.toString(),
        height = if (data.heightCm == 0) "" else data.heightCm.toString(),
        weight = if (data.weightKg == 0.0) "" else data.weightKg.toString(),
        year = data.productionYear,
        onWidthChange = { viewModel.updateArtworkData(widthCm = it.toIntOrNull() ?: 0) },
        onHeightChange = { viewModel.updateArtworkData(heightCm = it.toIntOrNull() ?: 0) },
        onWeightChange = { viewModel.updateArtworkData(weightKg = it.toDoubleOrNull() ?: 0.0) },
        onYearChange = { viewModel.updateArtworkData(productionYear = it) },
        onNext = onNext,
        modifier = modifier
    )
}

@Composable
fun ArtworkDetailInfoContent(
    width: String,
    height: String,
    weight: String,
    year: String,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).clearFocusOnTap()
    ) {
        Text(
            text = "상세 정보",
            fontFamily = jansilFont,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(24.dp))

        YeobaegOutlinedTextField(
            title = "가로(cm)",
            essential = true,
            value = width,
            onValueChange = onWidthChange,
            placeholder = "가로 입력",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        YeobaegOutlinedTextField(
            title = "세로(cm)",
            essential = true,
            value = height,
            onValueChange = onHeightChange,
            placeholder = "세로 입력",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        YeobaegOutlinedTextField(
            title = "무게(kg)",
            essential = true,
            value = weight,
            onValueChange = onWeightChange,
            placeholder = "무게 입력",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        YeobaegOutlinedTextField(
            title = "제작 연도",
            essential = true,
            value = year,
            onValueChange = onYearChange,
            placeholder = "연도 입력",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))
        YeobaegButton(
            text = "다음",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = width.isNotBlank() && height.isNotBlank() && weight.isNotBlank() && year.isNotBlank()
        )
    }
}
