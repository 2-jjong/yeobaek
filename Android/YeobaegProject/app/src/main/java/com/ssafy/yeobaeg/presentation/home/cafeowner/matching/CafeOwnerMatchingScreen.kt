package com.ssafy.yeobaeg.presentation.home.cafeowner.matching

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.presentation.arcore.plan.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.common.component.YeobaegButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.presentation.home.artist.matching.ArtworkList

@Composable
fun CafeOwnerMatchingScreen(
    slots: List<DisplaySlot>,
    viewModel: CafeOwnerMatchingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initSlots(slots)
    }

    when (state.mode) {
        MatchingMode.SELECT_SLOT -> SelectSlotScreen(
            slots = state.slots,
            currentWallId = state.currentWallId,
            onWallTapped = { id -> viewModel.selectWall(id) },
            onNext = { viewModel.moveToArtworkSelection() }
        )

        MatchingMode.SELECT_ARTWORK -> RecommendedArtworksScreen(
            state = state,
            onArtworkClick = viewModel::navigateToArtworkDetail,
            onLoadMore = viewModel::loadMoreArtworks,
            onBackClick = viewModel::backToSlotSelection
        )
    }
}

@Composable
private fun SelectSlotScreen(
    slots: List<DisplaySlot>,
    currentWallId: Long,
    onWallTapped: (Long) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text(text = "작품을 매칭할 슬롯을 선택하세요.", fontSize = 22.sp)
        Spacer(Modifier.height(20.dp))

        FloorPlanPreview(
            initialWalls = slots,
            readOnly = true,
            onWallTapped = onWallTapped,
            activeWallId = currentWallId,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(16.dp))

        YeobaegButton(
            text = "작품 선택",
            onClick = onNext,
            enabled = currentWallId != -1L,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecommendedArtworksScreen(
    state: MatchingUiState,
    onArtworkClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        YeobaegTopAppBar(
            title = "매칭할 작품 선택",
            onBackClick = onBackClick
        )

        ArtworkList(
            artworks = state.recommendedArtworks,
            isLoading = state.isLoadingArtworks,
            isLoadingMore = state.isLoadingMoreArtworks,
            hasNext = state.artworkPagination?.hasNext ?: false,
            onItemClick = onArtworkClick,
            onLoadMore = onLoadMore,
            selectedArtworkId = state.selectedArtworkId,
            modifier = Modifier.weight(1f)
        )
    }
}