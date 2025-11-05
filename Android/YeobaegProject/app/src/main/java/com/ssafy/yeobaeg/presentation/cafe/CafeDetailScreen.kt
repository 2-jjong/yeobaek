package com.ssafy.yeobaeg.presentation.cafe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.domain.model.cafe.*
import com.ssafy.yeobaeg.domain.model.cafe.SlotStatus.*
import com.ssafy.yeobaeg.domain.model.cafeowner.CafeOwner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeDetailScreen(
    cafeId: Long,
    viewModel: CafeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(cafeId) {
        viewModel.loadCafeDetail(cafeId)
    }
    
    Scaffold(
        topBar = {
            CafeDetailTopBar(onNavigateBack = viewModel::navigateBack)
        },
        bottomBar = {
            if (uiState.cafeDetail != null) {
                CafeDetailBottomBar(
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error ?: "오류가 발생했습니다",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.loadCafeDetail(cafeId) }) {
                            Text("다시 시도")
                        }
                    }
                }
                uiState.cafeDetail != null -> {
                    CafeDetailContent(
                        cafeDetail = uiState.cafeDetail!!,
                        selectedTab = uiState.selectedTab,
                        onTabSelected = viewModel::selectTab,
                        onBookmarkClick = viewModel::toggleBookmark
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeDetailTopBar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun CafeDetailContent(
    cafeDetail: CafeDetail,
    selectedTab: CafeDetailTab,
    onTabSelected: (CafeDetailTab) -> Unit,
    onBookmarkClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // 이미지 갤러리
        item {
            CafeImageGallery(
                images = cafeDetail.images
            )
        }
        
        // 카페 기본 정보
        item {
            CafeBasicInfo(
                cafeName = cafeDetail.cafeName,
                categories = cafeDetail.categories,
                isBookmarked = cafeDetail.isBookmarked,
                onBookmarkClick = onBookmarkClick
            )
        }
        
        // 탭
        item {
            CafeDetailTabs(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
        
        // 탭 내용
        item {
            when (selectedTab) {
                CafeDetailTab.EXHIBITION -> {
                    ExhibitionTabContent(
                        slots = cafeDetail.slots
                    )
                }
                CafeDetailTab.INFO -> {
                    InfoTabContent(
                        cafeDetail = cafeDetail
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeImageGallery(
    images: List<CafeImage>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // 이미지 페이저
        val sortedImages = images.sortedBy { it.displayOrder }
        val pagerState = rememberPagerState(pageCount = { sortedImages.size })
        
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model = sortedImages[page].imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        // 페이지 인디케이터
        if (sortedImages.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(sortedImages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    Color.White
                                else
                                    Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun CafeBasicInfo(
    cafeName: String,
    categories: List<String>,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 카페 이름과 북마크 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cafeName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onBookmarkClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "북마크",
                    tint = if (isBookmarked) Color.Red else Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF59D)
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CafeDetailTabs(
    selectedTab: CafeDetailTab,
    onTabSelected: (CafeDetailTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Tab(
            selected = selectedTab == CafeDetailTab.EXHIBITION,
            onClick = { onTabSelected(CafeDetailTab.EXHIBITION) },
            text = { Text("전시") }
        )
        Tab(
            selected = selectedTab == CafeDetailTab.INFO,
            onClick = { onTabSelected(CafeDetailTab.INFO) },
            text = { Text("정보") }
        )
    }
}

@Composable
fun ExhibitionTabContent(
    slots: List<SlotDetail>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (slots.isEmpty()) {
            Text(
                text = "등록된 전시 슬롯이 없습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        } else {
            slots.forEach { slot ->
                SlotItem(slot = slot)
            }
        }
    }
}

@Composable
fun SlotItem(slot: SlotDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 슬롯 이미지 또는 작품 이미지
            AsyncImage(
                model = slot.currentExhibition?.artworkImageUrl,
                contentDescription = slot.slotName,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            
            // 슬롯 정보
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = slot.currentExhibition?.artworkTitle ?: slot.slotName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (slot.currentExhibition != null) {
                        Text(
                            text = slot.currentExhibition.artistName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${slot.currentExhibition.exhibitionStartDate} - ${slot.currentExhibition.exhibitionEndDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "${slot.widthCm}cm × ${slot.heightCm}cm",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 상태 및 수수료
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (slot.status) {
                            AVAILABLE -> MaterialTheme.colorScheme.primaryContainer
                            EXHIBITING -> Color(0xFFFFF59D)
                            SCHEDULED -> MaterialTheme.colorScheme.errorContainer
                        }
                    ) {
                        Text(
                            text = when (slot.status) {
                                AVAILABLE -> "전시 가능"
                                EXHIBITING -> "전시 중"
                                SCHEDULED -> "예약 됨"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = when (slot.status) {
                                AVAILABLE -> MaterialTheme.colorScheme.onPrimaryContainer
                                EXHIBITING -> Color(0xFF5D4037)
                                SCHEDULED -> MaterialTheme.colorScheme.onErrorContainer
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = "수수료 ${slot.commissionRate}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun InfoTabContent(
    cafeDetail: CafeDetail
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 카페 소개
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "카페 소개",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = cafeDetail.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 카페 사장 정보
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "카페 사장 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            CafeOwnerCard(cafeOwner = cafeDetail.cafeOwner)
        }
        
        HorizontalDivider()
        
        // 평점
        InfoRow(
            icon = Icons.Default.Star,
            iconTint = Color(0xFFFFC107),
            text = "${cafeDetail.cafeOwner.ratingStats.averageRating} (${cafeDetail.cafeOwner.ratingStats.totalReviews})"
        )
        
        // 주소
        InfoRow(
            icon = Icons.Default.LocationOn,
            iconTint = Color(0xFFE53935),
            text = cafeDetail.address
        )
        
        // 영업시간
        InfoRow(
            icon = Icons.Default.AccessTime,
            iconTint = MaterialTheme.colorScheme.primary,
            text = "평일 ${cafeDetail.businessHoursStart}-${cafeDetail.businessHoursEnd}"
        )
        
        // 전화번호
        InfoRow(
            icon = Icons.Default.Phone,
            iconTint = MaterialTheme.colorScheme.primary,
            text = cafeDetail.contact
        )
    }
}

@Composable
fun CafeOwnerCard(
    cafeOwner: CafeOwner
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F1ED))
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            AsyncImage(
                model = cafeOwner.profileImageUrl,
                contentDescription = "사장님 프로필",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        
        // 사장 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = cafeOwner.nickname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            
            // 평점
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 평점 배지
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF9E6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${cafeOwner.ratingStats.averageRating}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                }
                
                Text(
                    text = "리뷰 ${cafeOwner.ratingStats.totalReviews}개",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    iconTint: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CafeDetailBottomBar(
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Button(
            onClick = { /* TODO: 채팅 하기 */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8D6E63)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "문의",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

