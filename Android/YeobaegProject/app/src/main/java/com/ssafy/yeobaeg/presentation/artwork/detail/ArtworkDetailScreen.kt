// presentation/artwork/detail/ArtworkDetailScreen.kt
package com.ssafy.yeobaeg.presentation.artwork.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.domain.model.art.ArtistSummary
import com.ssafy.yeobaeg.domain.model.art.ArtworkDetail
import com.ssafy.yeobaeg.domain.model.art.ExhibitionSpot
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.*

@Composable
fun ArtworkDetailScreen(
    artworkId: Long,
    viewModel: ArtworkDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(artworkId) { viewModel.load(artworkId) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.loading) {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YeobaegBrown
            )
        }
    }

    state.artwork?.let {
        ArtworkDetailScreen(
            artwork = it,
            onBackClick = viewModel::onBackClick,
            toggleBookmark = viewModel::toggleBookmark
        )
    }
}

@Composable
fun ArtworkDetailScreen(
    modifier: Modifier = Modifier,
    artwork: ArtworkDetail,
    onBackClick: () -> Unit = {},
    toggleBookmark: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작품 상세",
                onBackClick = onBackClick,
                actionIcon = if (artwork.isBookmarked) R.drawable.ic_heart else R.drawable.ic_heart,
                onActionClick = toggleBookmark
            )
        },
        bottomBar = {
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YeobaegDarkBrown),
                shape = RoundedCornerShape(12.dp)
            ) { Text("문의") }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // 1) 이미지 페이저
            ArtworkImagePager(artwork.images)

            // 2) 타이틀/카운트
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        artwork.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        painter = painterResource(R.drawable.ic_heart),
                        contentDescription = null,
                        tint = YeobaegDarkRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "${artwork.bookmarkCount}",
                        color = YeobaegDarkGray,
                        fontWeight = FontWeight.Light
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_view),
                        contentDescription = null,
                        tint = YeobaegDarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "${artwork.viewCount}",
                        color = YeobaegDarkGray,
                        fontWeight = FontWeight.Light
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(artwork.category, color = YeobaegBrown)
                Spacer(Modifier.height(8.dp))
            }

            // 3) 작가 카드
            ArtistCard(artwork.artist)

            // 4) 작품 정보
            InfoCard(
                price = artwork.price,
                sizeText = "${artwork.widthCm}cm x ${artwork.heightCm}cm",
                year = artwork.productionYear,
                weight = artwork.weightKg
            )
            HorizontalDivider(thickness = 0.5.dp, color = YeobaegLightGray)
            // 5) 작품 설명
            DescriptionCard(artwork.description)
            HorizontalDivider(thickness = 0.5.dp, color = YeobaegLightGray)
            // 6) 전시 장소
            artwork.exhibition?.let { ExhibitionCard(it) }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ArtworkImagePager(images: List<String>) {
    val pagerState = rememberPagerState { images.size.coerceAtLeast(1) }

    Box(
        Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val url = images.getOrNull(page)
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        // 인디케이터
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(images.size) { idx ->
                    val active = idx == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (active) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (active) YeobaegDarkBrown else Color(0x66FFFFFF))
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistCard(artist: ArtistSummary) {
    Surface(
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF9FAFB))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = artist.profileImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(artist.artistName, fontWeight = FontWeight.SemiBold)
                Text("작가", color = YeobaegTextGray)
            }
        }
    }
}

@Composable
private fun InfoCard(
    price: Int,
    sizeText: String,
    year: Int,
    weight: Double
) {
    Column(
        Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text("작품 정보", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        InfoRow("가격", price.formatPrice())
        InfoRow("크기", sizeText)
        InfoRow("제작년도", "$year")
        InfoRow("무게", "${weight.removeTrailingZero()}kg")
    }
}

@Composable
private fun DescriptionCard(text: String) {
    Column(Modifier.padding(24.dp)) {
        Text("작품 설명", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(text, color = YeobaegDarkGray, lineHeight = 20.sp)
    }
}

@Composable
private fun ExhibitionCard(ex: ExhibitionSpot) {
    Column(Modifier.padding(24.dp)) {
        Text("전시 장소", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ex.cafeImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ex.cafeName, fontWeight = FontWeight.SemiBold)
                Text(
                    ex.cafeAddress,
                    color = YeobaegTextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = YeobaegDarkGray)
        Text(value, color = YeobaegBlack, textAlign = TextAlign.End, fontWeight = FontWeight.Light)
    }
}

// --- 간단 유틸 ---
private fun Int.formatPrice(): String = "%,d원".format(this)
private fun Double.removeTrailingZero(): String {
    val s = toString()
    return if (s.endsWith(".0")) s.dropLast(2) else s
}
