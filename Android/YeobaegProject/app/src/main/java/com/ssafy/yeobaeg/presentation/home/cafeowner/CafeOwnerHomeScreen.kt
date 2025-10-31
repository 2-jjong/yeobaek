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
import com.ssafy.yeobaeg.presentation.arcore.FloorPlanPreview
import com.ssafy.yeobaeg.presentation.common.component.YeobaegOutlinedButton
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

    val colors = if (cafeOwnerInfo == null) RegisteredColors else UnregisteredColors
    CompositionLocalProvider(LocalCafeOwnerColors provides colors) {
        CafeOwnerHomeScreen(
            cafeOwnerInfo = cafeOwnerInfo,
            displaySlots = displaySlots
        )
    }
}

@Composable
private fun CafeOwnerHomeScreen(
    cafeOwnerInfo: CafeOwnerInfo?,
    displaySlots: List<DisplaySlot> = emptyList()
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        YBTopBar(title = "카페 관리", iconResId = R.drawable.ic_setting)
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

            CafeOwnerHomeActions(isNeedRegister = true)

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

                CafeOwnerHomeActions()
                Spacer(Modifier.height(8.dp))

                CafeOwnerHomeContent(displaySlots)
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
fun CafeOwnerHomeActions(isNeedRegister: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        if (isNeedRegister) YeobaegOutlinedButton(
            modifier = Modifier.weight(1f),
            text = "카페 등록",
            icon = R.drawable.ic_cafe_add
        )
        else {
            YeobaegOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "공간 스캔",
                icon = R.drawable.ic_cafe_add
            )
            Spacer(Modifier.width(16.dp))

            YeobaegOutlinedButton(
                modifier = Modifier.weight(1f),
                text = "작가 찾기",
                icon = R.drawable.ic_cafe_add
            )
        }
    }
}

enum class CafeOwnerTab(val label: String) {
    PLAN("공간 도면"),
    STATUS("전시 현황")
}

@Composable
fun CafeOwnerHomeContent(
    slots: List<DisplaySlot>
) {
    var tab by remember { mutableStateOf(CafeOwnerTab.PLAN) }

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
                    onClick = { tab = t },
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
            CafeOwnerTab.PLAN -> FloorPlanSection()
            CafeOwnerTab.STATUS -> ExhibitionStatusSection(slots = slots)
        }
    }
}

@Composable
fun FloorPlanSection() {
    FloorPlanPreview(Modifier.fillMaxWidth())
}

@Composable
fun ExhibitionStatusSection(slots: List<DisplaySlot>) {
    DisplaySlotList(slots = slots)
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


@Composable
fun YBTopBar(
    title: String,
    iconResId: Int,
    useSpace: Boolean = true,
    onBackPressed: (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackPressed != null) {
                IconButton(
                    onClick = { onBackPressed() }
                ) {
                    Icon(painter = painterResource(R.drawable.ic_back), contentDescription = null)
                }
                Spacer(Modifier.width(4.dp))
            }
            Spacer(Modifier.width(8.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = {}
            ) {
                Icon(painter = painterResource(iconResId), contentDescription = null)
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Color.LightGray,
            thickness = 1.dp
        )

        if (useSpace) Spacer(Modifier.height(16.dp))
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

