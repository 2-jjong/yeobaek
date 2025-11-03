package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.DisplayWall
import com.ssafy.yeobaeg.presentation.arcore.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
import com.ssafy.yeobaeg.presentation.common.component.YeobaegTopAppBar
import com.ssafy.yeobaeg.ui.theme.YeobaegBrown
import com.ssafy.yeobaeg.ui.theme.YeobaegDarkGray
import com.ssafy.yeobaeg.ui.theme.YeobaegGray
import com.ssafy.yeobaeg.ui.theme.YeobaegLightGray
import com.ssafy.yeobaeg.ui.theme.YeobaegProjectTheme

@Composable
fun CafeOwnerHomeScreen(
    viewModel: CafeOwnerHomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    val cafeOwnerInfo = state.value.cafeOwnerInfo
    val displaySlots = state.value.displaySlots
    val displayWalls = state.value.displayWalls
    val selectedTab = state.value.selectedTab

    val colors = if (cafeOwnerInfo == null) RegisteredColors else UnregisteredColors
    CompositionLocalProvider(LocalCafeOwnerColors provides colors) {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = cafeOwnerInfo,
            displaySlots = displaySlots,
            displayWalls = displayWalls,
            selectedTab = selectedTab,
            onRegisterClick = viewModel::navigateToRegister,
            onTabSelected = { viewModel.selectTab(it) },
            onScanClick = viewModel::navigateToSpaceScan,
            onSearchArtistClick = { /* TODO: 작가 찾기 클릭 처리 */ }
        )
    }
}

@Composable
private fun CafeOwnerHomeScreen(
    cafeOwnerInfo: CafeOwnerInfo?,
    displaySlots: List<DisplaySlot> = emptyList(),
    displayWalls: List<DisplayWall> = emptyList(),
    selectedTab: CafeOwnerTab = CafeOwnerTab.PLAN,
    onRegisterClick: () -> Unit = {},
    onTabSelected: (CafeOwnerTab) -> Unit = {},
    onScanClick: () -> Unit = {},
    onSearchArtistClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        YeobaegTopAppBar(
            title = "카페 관리",
            onBackClick = {},
            showBackButton = false,
            actionIcon = R.drawable.ic_setting,
            onActionClick = {},
        )
        Spacer(Modifier.height(4.dp))

        if (cafeOwnerInfo == null) {
            CafeOwnerHomeHeader(
                cafeImgUrl = R.drawable.ic_cafe,
                cafeName = "카페를 등록해 주세요.",
                cafeAddress = "-",
                displaySpace = "-",
                display = "-",
                collaboratingArtist = "-"
            )
            Spacer(Modifier.height(8.dp))

            CafeOwnerHomeActions(onRegisterClick = onRegisterClick)

            CafeOwnerHomeEmptyContent()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CafeOwnerHomeHeader(
                    cafeImgUrl = cafeOwnerInfo.cafeImageUrl,
                    cafeName = cafeOwnerInfo.cafeName,
                    cafeAddress = cafeOwnerInfo.cafeAddress,
                    displaySpace = cafeOwnerInfo.numExhibitionSpaces.toString(),
                    display = cafeOwnerInfo.numExhibitedWorks.toString(),
                    collaboratingArtist = cafeOwnerInfo.numArtistsWorkedWith.toString()
                )
                Spacer(Modifier.height(8.dp))

                CafeOwnerHomeActions(
                    onScanClick = onScanClick,
                    onSearchArtistClick = onSearchArtistClick
                )
                Spacer(Modifier.height(8.dp))

                CafeOwnerHomeContent(displaySlots, displayWalls, selectedTab, onTabSelected)
            }
        }
    }
}

@Composable
fun CafeOwnerHomeHeader(
    cafeImgUrl: Any,
    cafeName: String,
    cafeAddress: String,
    displaySpace: String,
    display: String,
    collaboratingArtist: String
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = cafeImgUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentScale = ContentScale.Crop
        )
    }
    Spacer(Modifier.height(4.dp))
    CafeTitle(cafeName = cafeName, cafeAddress = cafeAddress)
    Spacer(Modifier.height(4.dp))
    CafeDisplayStatus(
        displaySpace = displaySpace,
        display = display,
        collaboratingArtist = collaboratingArtist
    )
}

@Composable
fun CafeOwnerHomeActions(
    onRegisterClick: (() -> Unit)? = null,
    onScanClick: () -> Unit = {},
    onSearchArtistClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        if (onRegisterClick != null) YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "카페 등록",
            icon = R.drawable.ic_cafe_add,
            onClick = onRegisterClick
        )
        else {
            YeobaegOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "공간 스캔",
                icon = R.drawable.ic_cafe_add,
                onClick = onScanClick
            )
            Spacer(Modifier.width(16.dp))

            YeobaegOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "작가 찾기",
                icon = R.drawable.ic_cafe_add,
                onClick = onSearchArtistClick
            )
        }
    }
}

@Composable
fun CafeOwnerHomeContent(
    slots: List<DisplaySlot>,
    walls: List<DisplayWall>,
    tab: CafeOwnerTab,
    onTabSelected: (CafeOwnerTab) -> Unit = { }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 탭 헤더
        TabRow(
            selectedTabIndex = tab.ordinal,
            containerColor = Color.Transparent,
            indicator = {},
            divider = {}
        ) {
            CafeOwnerTab.entries.forEachIndexed { index, t ->
                val selected = tab.ordinal == index
                Tab(
                    selected = selected,
                    onClick = { onTabSelected(t) },
                    text = {
                        Text(
                            text = t.label,
                            fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = if (selected) Color.White else YeobaegDarkGray
                        )
                    },
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) YeobaegBrown else YeobaegLightGray)
                        .padding(horizontal = 12.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (tab) {
            CafeOwnerTab.PLAN -> FloorPlanSection(walls = walls)
            CafeOwnerTab.STATUS -> ExhibitionStatusSection(slots = slots)
        }
    }
}

@Composable
fun FloorPlanSection(walls: List<DisplayWall>) {
    Spacer(Modifier.height(8.dp))
    FloorPlanPreview(
        initialWalls = walls,
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        readOnly = true
    )
}

@Composable
fun ExhibitionStatusSection(slots: List<DisplaySlot>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DisplaySlotList(slots = slots)
    }
}

@Composable
fun DisplaySlotList(slots: List<DisplaySlot>) {
    slots.forEach { displaySlot ->
        ExhibitionListItem(
            thumbnail = displaySlot.artImgUrl,
            title = displaySlot.wName,
            status = displaySlot.status,
            workTitle = displaySlot.artTitle,
            artist = displaySlot.artist,
            endDate = displaySlot.deadline,
            sizeText = displaySlot.sizeText,
            onMatchClick = {}
        )
    }
}

@Composable
fun CafeOwnerHomeEmptyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(color = Color(0xFFFFFBEB), shape = RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "아직 등록한 카페가 없습니다.",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "카페를 등록하고 작가들과 함께\n특별한 전시 공간을 만들어보세요",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CafeOwnerHomeInitScreenPreview() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(null)
    }
}

@Preview(showBackground = true)
@Composable
fun CafeOwnerHomeScreenPreview() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(
            CafeOwnerInfo(
                cafeImageUrl = "R.drawable.ic_cafe",
                cafeName = "예쁜 카페",
                cafeAddress = "서울특별시 강남구 테헤란로 427",
                numExhibitionSpaces = 3,
                numExhibitedWorks = 15,
                numArtistsWorkedWith = 5
            )
        )
    }
}

@Preview(showBackground = true, name = "카페 등록 완료")
@Composable
private fun PreviewCafeOwnerHomeScreen_Registered_Plan() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = CafeOwnerInfo(
                cafeName = "아트카페 여백",
                cafeAddress = "서울시 강남구 테헤란로 123",
                cafeImageUrl = "",
                numExhibitionSpaces = 5,
                numExhibitedWorks = 12,
                numArtistsWorkedWith = 8
            ),
            displaySlots = dummyDisplaySlots,
            displayWalls = dummyDisplayWalls,
            selectedTab = CafeOwnerTab.PLAN
        )
    }
}

@Preview(showBackground = true, name = "카페 등록 완료")
@Composable
private fun PreviewCafeOwnerHomeScreen_Registered_Status() {
    YeobaegProjectTheme {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = CafeOwnerInfo(
                cafeName = "아트카페 여백",
                cafeAddress = "서울시 강남구 테헤란로 123",
                cafeImageUrl = "",
                numExhibitionSpaces = 5,
                numExhibitedWorks = 12,
                numArtistsWorkedWith = 8
            ),
            displaySlots = dummyDisplaySlots,
            displayWalls = dummyDisplayWalls,
            selectedTab = CafeOwnerTab.STATUS
        )
    }
}
