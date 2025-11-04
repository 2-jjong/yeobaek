package com.ssafy.yeobaeg.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.profile.ReviewStat
import com.ssafy.yeobaeg.domain.model.profile.ReviewStats
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite

// ------------------------------
// 실제 앱 실행용 (HiltViewModel 사용)
// ------------------------------
@Composable
fun ReviewManagementScreen(
    viewModel: ReviewManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadReviewStatistics()
    }

    ReviewManagementContent(
        uiState = uiState,
        onBackClick = viewModel::navigateBack,
        onReload = viewModel::loadReviewStatistics
    )
}

// ------------------------------
// UI 전용 (프리뷰 및 테스트에서 재사용 가능)
// ------------------------------
@Composable
fun ReviewManagementContent(
    uiState: ReviewStatsUiState,
    onBackClick: () -> Unit = {},
    onReload: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("받은 리뷰", "작성한 리뷰")

    val reviewStats = uiState.reviewStats ?: ReviewStats(
        statists = ReviewStat(
            userId = 1L,
            userName = "테스트유저",
            totalReviews = 60,
            okayCount = 12,
            goodCount = 45,
            badCount = 3,
            goodPercentage = 75.0
        )
    )

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "리뷰 관리",
                onBackClick = onBackClick
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = YeobaegWhite,
                contentColor = YeobaegBrown,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp),
                        color = YeobaegBrown
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier
                            .height(70.dp)
                            .padding(horizontal = 8.dp),
                        text = {
                            Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) YeobaegBrown else YeobaegGray
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> ReceivedReviewSummary(reviewStats = reviewStats)
                1 -> ReceivedReviewList()
            }
        }
    }
}

// ------------------------------
// 받은 리뷰 통계 요약
// ------------------------------
@Composable
fun ReceivedReviewSummary(reviewStats: ReviewStats) {
    val reviews = listOf(
        Triple("좋아요", reviewStats.statists.goodCount, Color(0xFFDFFFE0)),
        Triple("괜찮아요", reviewStats.statists.okayCount, Color(0xFFEAF2FF)),
        Triple("별로예요", reviewStats.statists.badCount, Color(0xFFFFE5E5))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        reviews.forEach { (title, count, bgColor) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFF2F2F2), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_heart
                    ),
                    contentDescription = title,
                    tint = when (title) {
                        "좋아요" -> Color(0xFF4CAF50)
                        "괜찮아요" -> Color(0xFF6495ED)
                        else -> Color(0xFFE57373)
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(bgColor, shape = CircleShape)
                        .padding(6.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = count.toString(),
                    fontSize = 18.sp,
                    color = when (title) {
                        "좋아요" -> Color(0xFF388E3C)
                        "괜찮아요" -> Color(0xFF1565C0)
                        else -> Color(0xFFD32F2F)
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ------------------------------
// 작성한 리뷰 리스트 (더미)
// ------------------------------
data class ReviewItem(
    val cafeName: String,
    val date: String,
    val comment: String,
)

@Composable
fun ReceivedReviewList() {
    val reviews = listOf(
        ReviewItem("Brew & Canvas", "2024.03.14", "친절하고 매너가 좋아요"),
        ReviewItem("Art Space Cafe", "2024.03.11", "시간 약속을 잘 지켜요"),
        ReviewItem("Gallery Lounge", "2024.03.08", "전문적이고 신뢰할 수 있어요"),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reviews) { review ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = review.cafeName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = review.date,
                            fontSize = 12.sp,
                            color = YeobaegGray
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF8DC), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = review.comment,
                            fontSize = 13.sp,
                            color = Color(0xFF754106)
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------
// Preview용 (Hilt 없이 mock state 전달)
// ------------------------------
@Preview(showBackground = true)
@Composable
fun ReviewManagementScreenPreview() {
    val fakeState = ReviewStatsUiState(
        isLoading = false,
        reviewStats = ReviewStats(
            statists = ReviewStat(
                userId = 1L,
                userName = "프리뷰유저",
                totalReviews = 60,
                okayCount = 12,
                goodCount = 45,
                badCount = 3,
                goodPercentage = 75.0
            )
        )
    )

    ReviewManagementContent(
        uiState = fakeState,
        onBackClick = {},
        onReload = {}
    )
}
