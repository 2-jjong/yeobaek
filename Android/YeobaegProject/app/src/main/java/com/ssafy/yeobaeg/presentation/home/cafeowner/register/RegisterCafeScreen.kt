// RegisterCafeScreen.kt
package com.ssafy.yeobaeg.presentation.home.cafeowner.register

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegStepBar
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTextField
import com.ssafy.yeobaeg.presentation.common.ui.SectionHeader
import com.ssafy.yeobaeg.presentation.common.util.rememberMultiImagePicker
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightYellow

@Composable
fun RegisterCafeScreen(
    viewModel: RegisterCafeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var showPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (state.step != STEP_ADDRESS) YeobaegStepBar(
                step = state.step,
                totalStep = TOTAL_STEPS
            )
        },
        bottomBar = {
            if (state.step != STEP_ADDRESS) BottomBarScaffold(
                step = state.step,
                enabled = if (state.step == STEP1) state.isStep1Valid else state.isStep2Valid,
                onPrev = { viewModel.prev() },
                onNext = { viewModel.next() }
            )
        },
        containerColor = Color.White
    ) { inner ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    .then(if(state.step>0) Modifier.verticalScroll(scroll) else Modifier),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (state.step) {
                    STEP1 -> Step1BasicInfo(
                        state = state,
                        onName = { viewModel.update { copy(name = it) } },
                        onDesc = { viewModel.update { copy(description = it.take(500)) } },
                        onPhone = { viewModel.update { copy(phone = it) } },
                        onOpen = { viewModel.update { copy(openTime = it) } },
                        onClose = { viewModel.update { copy(closeTime = it) } },
                        onAddImage = { viewModel.addImage(it) },
                        onRemoveImage = { viewModel.removeImage(it) },
                        showAddressPicker = { viewModel.update { copy(step = STEP_ADDRESS) } }
                    )

                    STEP2 -> Step2Attributes(
                        state = state,
                        onToggleStyle = { viewModel.toggleStyle(it) },
                        onVisitors = { viewModel.selectVisitorsBand(it) }
                    )

                    STEP_ADDRESS -> PostcodeJibunWebView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(inner),
                        onClose = {
                            Log.d("onClose webview", "")
                            viewModel.update { copy(step = STEP1) }
                        },
                        onSelected = { selected ->
                            Log.d("onSelected webview", selected)
                            viewModel.update { copy(address = selected, step = STEP1) }
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BottomBarScaffold(
    step: Int,
    enabled: Boolean,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (step == STEP2) {
            YeobaegOutlinedButton(
                text = "이전",
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                onClick = onPrev
            )
        }
        YeobaegButton(
            text = if (step == STEP1) "다음" else "완료",
            modifier = Modifier.weight(1f),
            onClick = onNext,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1BasicInfo(
    state: RegisterCafeState,
    onName: (String) -> Unit,
    onDesc: (String) -> Unit,
    onPhone: (String) -> Unit,
    onOpen: (String) -> Unit,
    onClose: (String) -> Unit,
    onAddImage: (String) -> Unit,
    onRemoveImage: (String) -> Unit,
    showAddressPicker: () -> Unit
) {
    SectionHeader(
        title = "기본 정보 입력",
        subtitle = "매장의 기본 정보를 입력해 주세요."
    )
    Spacer(Modifier.height(20.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        YeobaegTextField(
            value = state.name,
            onValueChange = onName,
            label = "매장명 *",
            placeholder = "Gallery Café Muse"
        )
        Spacer(Modifier.height(8.dp))
        YeobaegTextField(
            value = state.address,
            onValueChange = { },
            label = "주소 *",
            readOnly = true,
            placeholder = "서울시 강남구 신사동 123-45",
            onFocusTextField = showAddressPicker
        )
        Spacer(Modifier.height(8.dp))
        YeobaegTextField(
            value = state.description,
            onValueChange = onDesc,
            label = "매장 소개",
            placeholder = "예술과 커피가 만나는 특별한 공간입니다...",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            singleLine = false,
            minLine = 4,
            maxLine = 8
        )
        Spacer(Modifier.height(8.dp))
        YeobaegTextField(
            value = state.phone,
            onValueChange = onPhone,
            label = "연락처 *",
            placeholder = "02-1234-5678"
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                YeobaegTextField(
                    value = state.openTime,
                    onValueChange = onOpen,
                    label = "오픈 시간 *",
                    placeholder = "08:00"
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                YeobaegTextField(
                    value = state.closeTime,
                    onValueChange = onClose,
                    label = "마감 시간 *",
                    placeholder = "22:00"
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        ImagePickerBox(
            images = state.images.toList(),
            onAdd = onAddImage,
            onRemove = onRemoveImage
        )
    }
}

@Composable
private fun ImagePickerBox(
    images: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    val pickImages = rememberMultiImagePicker(max = 10) { uris ->
        uris.forEach { onAdd(it.toString()) }
    }

    val border = BorderStroke(1.dp, Color(0xFFE0E3E8))
    Text(text = "매장 이미지 추가", fontSize = 14.sp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(border, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(images) { uri ->
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F3F5))
                        .clickable { onRemove(uri) },
                    contentAlignment = Alignment.Center
                ) { AsyncImage(model = uri, contentDescription = null) }
            }
            item {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9FAFB))
                        .border(BorderStroke(1.dp, Color(0xFFE0E3E8)), RoundedCornerShape(12.dp))
                        .clickable { pickImages() },
                    contentAlignment = Alignment.Center
                ) { Text("이미지\n추가", color = Color(0xFF9EA4AA)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Attributes(
    state: RegisterCafeState,
    onToggleStyle: (CafeInteriorStyle) -> Unit,
    onVisitors: (VisitorsBand) -> Unit
) {
    SectionHeader(
        title = "매장 특성",
        subtitle = "매장의 특성과 분위기를 알려주세요."
    )
    Spacer(Modifier.height(20.dp))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "인터리어 스타일 (다중 선택 가능, 최대 3개) *", fontSize = 14.sp)

        val styles = CafeInteriorStyle.entries.toList()
        styles.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { style ->
                    val selected = style in state.selectedStyles
                    val limitReached = state.selectedStyles.size >= 3 && !selected
                    FilterChip(
                        selected = selected,
                        onClick = { if (!limitReached) onToggleStyle(style) },
                        enabled = !limitReached,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp),
                        colors = FilterChipDefaults.filterChipColors().copy(
                            selectedContainerColor = YeobaegLightYellow
                        ),
                        label = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = style.label, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }

        Text(
            text = "선택된 스타일: ${state.selectedStyles.size} / 3",
            fontSize = 12.sp,
            color = Color(0xFF7A7F87),
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(Modifier.height(8.dp))
        Text(text = "일일 평균 방문객 수 *", fontSize = 14.sp)

        var expanded by remember { mutableStateOf(false) }
        val visitorText = state.visitorsBand?.label ?: "선택해주세요"
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = visitorText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable)
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = YeobaegLightGray,
                    focusedBorderColor = Color.Black,
                    focusedContainerColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White
            ) {
                VisitorsBand.entries.forEach { band ->
                    DropdownMenuItem(
                        text = { Text(band.label) },
                        onClick = {
                            onVisitors(band)
                            expanded = false
                        },
                        colors = MenuDefaults.itemColors(textColor = Color(0xFF111827))
                    )
                }
            }
        }
    }
}

// --------------------------- 프리뷰 ---------------------------
@Preview(showBackground = true, name = "Step 1 - 기본 정보 입력")
@Composable
private fun PreviewRegisterCafeScreen_Step1() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Step1BasicInfo(
                state = RegisterCafeState(
                    step = 1,
                    name = "",
                    address = "",
                    description = "",
                    phone = "",
                    openTime = "",
                    closeTime = "",
                    images = emptySet()
                ),
                onName = {},
                onDesc = {},
                onPhone = {},
                onOpen = {},
                onClose = {},
                onAddImage = {},
                onRemoveImage = {},
                showAddressPicker = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Step 1 - 정보 입력 중")
@Composable
private fun PreviewRegisterCafeScreen_Step1_Filled() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Step1BasicInfo(
                state = RegisterCafeState(
                    step = 1,
                    name = "아트카페 여백",
                    address = "서울시 강남구 신사동 123-45",
                    description = "예술과 커피가 만나는 특별한 공간입니다. 현대 미술 작품과 함께 여유로운 시간을 보내세요.",
                    phone = "02-1234-5678",
                    openTime = "09:00",
                    closeTime = "22:00",
                    images = setOf("image1", "image2", "image3")
                ),
                onName = {},
                onDesc = {},
                onPhone = {},
                onOpen = {},
                onClose = {},
                onAddImage = {},
                onRemoveImage = {},
                showAddressPicker = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Step 2 - 매장 특성")
@Composable
private fun PreviewStep2Attributes_Filled() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            Step2Attributes(
                state = RegisterCafeState(
                    step = 2,
                    selectedStyles = setOf(
                        CafeInteriorStyle.MODERN,
                        CafeInteriorStyle.MINIMAL,
                        CafeInteriorStyle.INDUSTRIAL
                    ),
                    visitorsBand = VisitorsBand.OVER_500
                ),
                onToggleStyle = {},
                onVisitors = {}
            )
        }
    }
}
