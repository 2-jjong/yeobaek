package com.ssafy.yeobaeg.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.BookmarkArtworkSummary
import com.ssafy.yeobaeg.domain.model.BookmarkCafeSummary
import com.ssafy.yeobaeg.presentation.common.component.PaginatedList
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBlack
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegTextGray
import com.ssafy.yeobaeg.ui.theme.YeobaegWhite
import com.ssafy.yeobaeg.ui.theme.jansilFont


@Composable
fun BookmarkScreen(
    bookmarkViewModel: BookmarkViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by bookmarkViewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }
    val artworks = uiState.bookmarkArtwork?.bookmarkArtworks ?: emptyList()
    val cafes = uiState.bookmarkCafe?.bookmarkCafes ?: emptyList()
    val tabs = listOf("작품", "카페")

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "저장 목록",
                onBackClick = profileViewModel::navigateBack,
                actionIcon = null,
                onActionClick = {}
            )
        },
        containerColor = YeobaegWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(YeobaegWhite)
                .padding(paddingValues)
        ) {
            // 탭
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = YeobaegWhite,
                contentColor = YeobaegBlack,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)),
                        color = YeobaegBrown
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = jansilFont,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) YeobaegBrown else YeobaegTextGray
                            )
                        },
                        selectedContentColor = YeobaegBrown
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 탭별 리스트
            when (selectedTab) {
                0 -> ArtworkList(
                    items = artworks,
                    isLoading = uiState.isLoading,
                    hasNext = uiState.bookmarkArtwork?.pagination?.hasNext ?: false,
                    onLoadMore = { bookmarkViewModel.loadNextArtworkPage() }
                )

                1 -> {
                    // 카페 탭 선택 시 데이터가 없으면 자동 로드
                    if (cafes.isEmpty() && !uiState.isLoading) {
                        LaunchedEffect(Unit) {
                            bookmarkViewModel.loadBookmarkCafes()
                        }
                    }
                    CafeList(
                        items = cafes,
                        isLoading = uiState.isLoading,
                        hasNext = uiState.bookmarkCafe?.pagination?.hasNext ?: false,
                        onLoadMore = { bookmarkViewModel.loadNextCafePage() }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtworkList(
    items: List<BookmarkArtworkSummary>,
    isLoading: Boolean,
    hasNext: Boolean,
    onLoadMore: () -> Unit
) {
    PaginatedList(
        items = items,
        isLoading = isLoading,
        hasNext = hasNext,
        onLoadMore = onLoadMore
    ) { item ->
        SavedCard(
            title = item.title,
            des = item.artistName,
            imageUrl = item.thumbnailUrl
        )
    }
}

@Composable
fun CafeList(
    items: List<BookmarkCafeSummary>,
    isLoading: Boolean,
    hasNext: Boolean,
    onLoadMore: () -> Unit
) {
    PaginatedList(
        items = items,
        isLoading = isLoading,
        hasNext = hasNext,
        onLoadMore = onLoadMore
    ) { item ->
        SavedCard(
            title = item.cafeName,
            des = item.address,
            imageUrl = item.primaryImageUrl
        )
    }
}

@Composable
fun SavedCard(
    title: String,
    des: String,
    imageUrl: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(YeobaegWhite)
            .border(1.dp, YeobaegLightGray, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = YeobaegBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = des,
                fontFamily = jansilFont,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = YeobaegTextGray
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_fill_heart),
            contentDescription = "Heart",
            tint = Color(0xFFFF5A5A),
            modifier = Modifier.size(40.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SavedListScreenPreview() {
    BookmarkScreen()
}
