// ArtistHomeScreen.kt
package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ssafy.yeobaeg.R
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar

@Composable
fun ArtistHomeScreen(
    viewModel: ArtistHomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadArtistData()
    }

    ArtistHomeScreen(
        state = state,
        onUploadClick = {},
        onFindCafeClick = {},
        onWorkClick = {}
    )
}

@Composable
fun ArtistHomeScreen(
    state: ArtistHomeState,
    onUploadClick: () -> Unit = {},
    onFindCafeClick: () -> Unit = {},
    onWorkClick: (Long) -> Unit = {}
) {
    Scaffold(
        topBar = {
            YeobaegTopAppBar(
                title = "작가 홈",
                onBackClick = {},
                showBackButton = false,
                actionIcon = R.drawable.ic_setting,
                onActionClick = {}
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = inner.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(20.dp))
                ArtistProfileHeader(
                    name = state.name,
                    profileImageUrl = state.profileImageUrl,
                    worksCount = state.worksCount,
                    activeExhibitions = state.activeExhibitions
                )
            }

            item {
                ArtistQuickActions(
                    onUploadClick = onUploadClick,
                    onFindCafeClick = onFindCafeClick
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                Text(
                    text = "내 인기 작품 현황",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(state.artworks, key = { it.id }) { art ->
                ArtistArtworkItem(
                    artwork = art,
                    onClick = { onWorkClick(art.id) }
                )
            }
        }
    }
}

/* ───────────── Header ───────────── */

@Composable
private fun ArtistProfileHeader(
    name: String,
    profileImageUrl: String?,
    worksCount: Int,
    activeExhibitions: Int
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = profileImageUrl ?: R.drawable.ic_art,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F2F5)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(12.dp))
        Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCell(top = worksCount.toString(), bottom = "작품")
            StatCell(top = activeExhibitions.toString(), bottom = "전시 중", emphasis = true)
        }
    }
}

@Composable
private fun StatCell(top: String, bottom: String, emphasis: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = top,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (emphasis) Color(0xFF3B82F6) else Color.Black
        )
        Spacer(Modifier.height(2.dp))
        Text(text = bottom, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color(0xFF7A7F87))
    }
}

/* ───────────── Quick actions ───────────── */

@Composable
private fun ArtistQuickActions(
    onUploadClick: () -> Unit,
    onFindCafeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "작품 업로드",
            icon = R.drawable.ic_cafe_add,
            onClick = onUploadClick
        )
        YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "카페 찾기",
            icon = R.drawable.ic_search,
            onClick = onFindCafeClick
        )
    }
}

/* ───────────── Artwork item ───────────── */

@Composable
private fun ArtistArtworkItem(
    artwork: ArtistArtwork,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = artwork.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F3F5)),
            contentScale = ContentScale.Crop // ✅ 이미지 크롭
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = artwork.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                StatusChip(artwork.status)
            }

            if (!artwork.placeName.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = artwork.placeName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF7A7F87),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(4.dp))
            // ✅ 가격(좌) · 좋아요(우) 같은 줄
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = artwork.priceWon, fontSize = 13.sp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_fill_heart),
                        contentDescription = null,
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(text = artwork.likes.toString(), fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
            }
        }
    }
}

/* ───────────── Status chip ───────────── */

@Composable
private fun StatusChip(status: ArtistWorkStatus) {
    val (bg, fg) = when (status) {
        ArtistWorkStatus.ONGOING   -> Color(0xFFE6F6E9) to Color(0xFF22A163)
        ArtistWorkStatus.AVAILABLE -> Color(0xFFEAF2FF) to Color(0xFF3B82F6)
        ArtistWorkStatus.REVIEW    -> Color(0xFFFFF2CC) to Color(0xFFB58100)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
    ) {
        Text(
            text = status.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
