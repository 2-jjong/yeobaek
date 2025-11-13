package com.ssafy.yeobaeg.presentation.exhibition

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegTextPrimary
import com.ssafy.yeobaeg.ui.theme.YeobaegTextSecondary
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.ArtworkCategory
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionSchedule
import com.ssafy.yeobaeg.domain.model.exhibition.ExhibitionState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExhibitionCalendarScreen(
    slotId: Long,
    slotName: String = "전시 슬롯",
    viewModel: ExhibitionCalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(slotId) {
        viewModel.loadSchedule(slotId)
    }

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "$slotName 전시 일정",
                onBackClick = { viewModel.navigateBack() }
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error ?: "오류가 발생했습니다",
                        onRetry = { viewModel.loadSchedule(slotId) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    if (uiState.schedules.isEmpty()) {
                        EmptyScheduleContent()
                    } else {
                        CalendarContent(
                            schedules = uiState.schedules,
                            startDate = uiState.startDate,
                            selectedDate = uiState.selectedDate,
                            onDateSelected = viewModel::selectDate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Text("다시 시도")
        }
    }
}

@Composable
private fun EmptyScheduleContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "예정된 전시가 없습니다",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "이 슬롯에는 아직 전시 일정이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarContent(
    schedules: List<ExhibitionSchedule>,
    startDate: LocalDate,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    // 다이얼로그 표시 여부
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 30일간의 날짜 생성
        val dates = (0 until 30).map { startDate.plusDays(it.toLong()) }

        // 월별로 그룹화
        val monthGroups = dates.groupBy { YearMonth.from(it) }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            monthGroups.forEach { (yearMonth, datesInMonth) ->
                item {
                    MonthCalendar(
                        yearMonth = yearMonth,
                        dates = datesInMonth,
                        schedules = schedules,
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            onDateSelected(date)
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    // 날짜 선택 시 다이얼로그 표시
    if (showDialog && selectedDate != null) {
        ExhibitionListDialog(
            selectedDate = selectedDate,
            schedules = schedules,
            onDismiss = {
                showDialog = false
            }
        )
    }
}

@Composable
private fun MonthCalendar(
    yearMonth: YearMonth,
    dates: List<LocalDate>,
    schedules: List<ExhibitionSchedule>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    Column {
        // 월 헤더
        Text(
            text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 요일 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = when (day) {
                        "일" -> Color.Red
                        "토" -> Color.Blue
                        else -> Color.Gray
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 캘린더 그리드
        val firstDayOfMonth = yearMonth.atDay(1)
        // DayOfWeek: MONDAY=1, TUESDAY=2, ..., SUNDAY=7
        // 우리 요일 헤더: 일(0), 월(1), 화(2), ..., 토(6)
        val firstDayOfWeek = if (firstDayOfMonth.dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            0
        } else {
            firstDayOfMonth.dayOfWeek.value
        }
        val daysInMonth = yearMonth.lengthOfMonth()

        // 이번 달의 모든 날짜
        val allDatesInMonth = (1..daysInMonth).map { yearMonth.atDay(it) }

        // 주 단위로 그룹화
        val weeks = mutableListOf<List<LocalDate?>>()
        var currentWeek = MutableList<LocalDate?>(7) { null }

        // 첫 주의 빈 칸 채우기
        var dayIndex = firstDayOfWeek

        allDatesInMonth.forEach { date ->
            currentWeek[dayIndex] = date
            dayIndex++

            if (dayIndex == 7) {
                weeks.add(currentWeek.toList())
                currentWeek = MutableList(7) { null }
                dayIndex = 0
            }
        }

        // 마지막 주 추가
        if (currentWeek.any { it != null }) {
            weeks.add(currentWeek.toList())
        }

        // 주별로 표시
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    if (date != null) {
                        CalendarDayCell(
                            date = date,
                            schedules = schedules,
                            isSelected = date == selectedDate,
                            onDateSelected = onDateSelected,
                            isInRange = date in dates,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    schedules: List<ExhibitionSchedule>,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    isInRange: Boolean,
    modifier: Modifier = Modifier
) {
    val exhibitionsOnDate = if (isInRange) {
        schedules.filter { schedule ->
            val start = LocalDate.parse(schedule.startDate)
            val end = LocalDate.parse(schedule.endDate)
            !date.isBefore(start) && !date.isAfter(end)
        }
    } else {
        emptyList()
    }

    val hasExhibition = exhibitionsOnDate.isNotEmpty()
    val backgroundColor = when {
        !isInRange -> Color.Transparent
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        hasExhibition -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(enabled = hasExhibition && isInRange) {
                onDateSelected(date)
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (hasExhibition) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    !isInRange -> Color.LightGray.copy(alpha = 0.3f)
                    !hasExhibition -> Color.LightGray
                    date.dayOfWeek == java.time.DayOfWeek.SUNDAY -> Color.Red
                    date.dayOfWeek == java.time.DayOfWeek.SATURDAY -> Color.Blue
                    else -> Color.Black
                }
            )

            if (hasExhibition) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            when (exhibitionsOnDate.first().exhibitionState) {
                                ExhibitionState.ONGOING -> Color(0xFF4CAF50)
                                ExhibitionState.SCHEDULED -> Color(0xFF2196F3)
                                ExhibitionState.COMPLETED -> Color.Gray
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun ExhibitionListDialog(
    selectedDate: LocalDate,
    schedules: List<ExhibitionSchedule>,
    onDismiss: () -> Unit
) {
    val exhibitionsOnDate = schedules.filter { schedule ->
        val start = LocalDate.parse(schedule.startDate)
        val end = LocalDate.parse(schedule.endDate)
        !selectedDate.isBefore(start) && !selectedDate.isAfter(end)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = YeobaegWhite,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 제목
                Text(
                    text = "${selectedDate.format(DateTimeFormatter.ofPattern("M월 d일 (E)", Locale.KOREAN))} 전시",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = YeobaegTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 전시 목록 또는 빈 상태 메시지
                if (exhibitionsOnDate.isEmpty()) {
                    Text(
                        text = "이 날짜에는 전시가 없습니다",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = YeobaegTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(exhibitionsOnDate) { exhibition ->
                            ExhibitionCard(exhibition = exhibition)
                            if (exhibition != exhibitionsOnDate.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color(0xFFE0E0E0)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 확인 버튼
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YeobaegDarkBrown,
                        contentColor = YeobaegWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExhibitionCard(exhibition: ExhibitionSchedule) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exhibition.artwork.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            ExhibitionStateBadge(state = exhibition.exhibitionState)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = exhibition.artwork.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val categoryLabel = ArtworkCategory.values()
                .find { it.name == exhibition.artwork.artCategory }?.label
                ?: exhibition.artwork.artCategory

            Text(
                text = "카테고리: $categoryLabel",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        val startDate = LocalDate.parse(exhibition.startDate)
        val endDate = LocalDate.parse(exhibition.endDate)
        Text(
            text = "전시 기간: ${startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))} ~ ${endDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun ExhibitionStateBadge(state: ExhibitionState) {
    val (text, color) = when (state) {
        ExhibitionState.ONGOING -> "진행중" to Color(0xFF4CAF50)
        ExhibitionState.SCHEDULED -> "예정" to Color(0xFF2196F3)
        ExhibitionState.COMPLETED -> "완료" to Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
