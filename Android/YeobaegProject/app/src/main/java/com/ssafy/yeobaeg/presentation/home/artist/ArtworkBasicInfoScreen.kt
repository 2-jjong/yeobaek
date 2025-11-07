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
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedTextField
import com.ssafy.yeobaeg.presentation.common.util.clearFocusOnTap
import com.ssafy.yeobaeg.presentation.common.util.formatPriceInput
import com.ssafy.yeobaeg.presentation.home.artist.component.ArtworkCategorySelector
import com.ssafy.yeobaeg.ui.theme.jansilFont

@Composable
fun ArtworkBasicInfoScreen(
    onNext: () -> Unit,
    viewModel: ArtworkRegisterViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val data = uiState.artworkData

    ArtworkBasicInfoContent(
        title = data.title,
        category = data.category,
        price = if (data.price == 0) "" else formatPriceInput(data.price.toString()),
        onTitleChange = { viewModel.updateArtworkData(title = it) },
        onCategorySelect = { viewModel.updateArtworkData(category = it) },
        onPriceChange = { formatted ->
            val digitsOnly = formatted.filter { it.isDigit() }
            viewModel.updateArtworkData(price = digitsOnly.toIntOrNull() ?: 0)
        },
        onNext = onNext,
        modifier = modifier
    )
}

@Composable
fun ArtworkBasicInfoContent(
    title: String,
    category: ArtworkCategory,
    price: String,
    onTitleChange: (String) -> Unit,
    onCategorySelect: (ArtworkCategory) -> Unit,
    onPriceChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .clearFocusOnTap()
    ) {
        Text(
            text = "기본 정보",
            fontSize = 18.sp,
            fontFamily = jansilFont,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(20.dp))

        // 제목 입력
        YeobaegOutlinedTextField(
            title = "작품 제목",
            essential = true,
            value = title,
            onValueChange = onTitleChange,
            placeholder = "제목을 입력하세요",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // 카테고리 선택 (Enum 기반)
        ArtworkCategorySelector(
            selectedCategory = category,
            onSelect = onCategorySelect
        )

        Spacer(Modifier.height(12.dp))

        // 가격 입력
        YeobaegOutlinedTextField(
            title = "가격",
            essential = true,
            unit = "원",
            value = price,
            onValueChange = { input ->
                val formatted = formatPriceInput(input)
                onPriceChange(formatted)
            },
            placeholder = "가격을 입력하세요",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        // 다음 버튼
        YeobaegButton(
            text = "다음",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank() && price.isNotBlank()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtworkBasicInfoScreenPreview() {
    ArtworkBasicInfoContent(
        title = "햇살이 스미는 풍경",
        category = ArtworkCategory.LANDSCAPE,
        price = "150000",
        onTitleChange = {},
        onCategorySelect = {},
        onPriceChange = {},
        onNext = {}
    )
}
