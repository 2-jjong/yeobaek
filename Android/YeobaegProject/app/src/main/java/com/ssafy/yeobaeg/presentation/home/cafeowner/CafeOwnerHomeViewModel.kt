package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.DisplayWall
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.domain.model.Vec2
import com.ssafy.yeobaeg.domain.model.WallStatus
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CafeOwnerHomeViewModel @Inject constructor(
    private val navigator: Navigator
): ViewModel() {

    private var _uiState = MutableStateFlow(CafeOwnerHomeUiState())
    val uiState = _uiState.asStateFlow()

    fun selectTab(tab: CafeOwnerTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun navigateToSpaceScan() = viewModelScope.launch {
        navigator.navigate(
            FullScreen.SpaceScan
        )
    }

    fun navigateToRegister() = viewModelScope.launch {
        navigator.navigate(
            FullScreen.RegisterCafe
        )
    }
}

val dummyCafeOwnerInfo = CafeOwnerInfo(
    cafeName = "예술카페",
    cafeImageUrl = "https://example.com/cafe.jpg",
    cafeAddress = "서울특별시 강남구 예술로 123",
    numExhibitionSpaces = 4,
    numExhibitedWorks = 12,
    numArtistsWorkedWith = 5
)

val dummyDisplaySlots = listOf(
    DisplaySlot(
        wid = 1L,
        wName = "입구 좌측 벽",
        width = 120f,
        height = 80f,
        status = SlotStatus.AVAILABLE,
        commission = 15f,
        requirements = "실내 조명 밝음, 습도 조절 필요",
        deadline = "",
        artId = 0L,
        artTitle = "푸른 밤의 꿈",
        artist = "김예술",
        artImgUrl = "https://example.com/artwork1.jpg"
    ),
    DisplaySlot(
        wid = 2L,
        wName = "메인 전시 공간",
        width = 150f,
        height = 100f,
        status = SlotStatus.ONGOING,
        commission = 20f,
        requirements = "대형 작품 가능",
        deadline = "2024-12-31",
        artId = 101L,
        artTitle = "푸른 밤의 꿈",
        artist = "김예술",
        artImgUrl = "https://example.com/artwork1.jpg"
    ),
    DisplaySlot(
        wid = 3L,
        wName = "창가 벽면",
        width = 100f,
        height = 100f,
        status = SlotStatus.RESERVED,
        commission = 12f,
        requirements = "자연광 유입",
        deadline = "2025-01-15",
        artId = 0L,
        artTitle = "푸른 밤의 꿈",
        artist = "김예술",
        artImgUrl = "https://example.com/artwork1.jpg"
    ),
    DisplaySlot(
        wid = 4L,
        wName = "복도 우측",
        width = 90f,
        height = 120f,
        status = SlotStatus.ONGOING,
        commission = 10f,
        requirements = "세로형 작품 추천",
        deadline = "2025-02-28",
        artId = 102L,
        artTitle = "도시의 리듬",
        artist = "박작가",
        artImgUrl = "https://example.com/artwork2.jpg"
    )
)

val dummyDisplayWalls = listOf(
    DisplayWall(
        id = 1L,
        wName = "입구 좌측 벽",
        start = Vec2(0f, 0f),
        end = Vec2(8f, 0f),
        status = WallStatus.POSSIBLE
    ),
    DisplayWall(
        id = 2L,
        wName = "메인 전시 공간",
        start = Vec2(8f, 0f),
        end = Vec2(8f, 12f),
        status = WallStatus.IN_USE
    ),
    DisplayWall(
        id = 3L,
        wName = "창가 벽면",
        start = Vec2(8f, 12f),
        end = Vec2(4f, 15f),
        status = WallStatus.RESERVED
    ),
    DisplayWall(
        id = 4L,
        wName = "복도 우측",
        start = Vec2(4f, 15f),
        end = Vec2(0f, 12f),
        status = WallStatus.IN_USE
    ),
    DisplayWall(
        id = 5L,
        wName = "후면 벽",
        start = Vec2(0f, 12f),
        end = Vec2(0f, 0f),
        status = WallStatus.POSSIBLE
    )
)