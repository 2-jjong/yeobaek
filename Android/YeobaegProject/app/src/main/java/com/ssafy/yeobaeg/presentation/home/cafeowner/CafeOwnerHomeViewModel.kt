package com.ssafy.yeobaeg.presentation.home.cafeowner

import androidx.lifecycle.ViewModel
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


data class CafeOwnerInfo(
    val cafeName: String = "test",
    val cafeImageUrl: String = "",
    val cafeAddress: String = "address",
    val numExhibitionSpaces: Int = 0,
    val numExhibitedWorks: Int = 0,
    val numArtistsWorkedWith: Int = 0,
)

data class CafeOwnerHomeUiState(
    val cafeOwnerInfo: CafeOwnerInfo? = null,
    val displaySlots: List<DisplaySlot> = dummyDisplaySlots
)

@HiltViewModel
class CafeOwnerHomeViewModel @Inject constructor(): ViewModel() {

    private var _uiState = MutableStateFlow(CafeOwnerHomeUiState())
    val uiState = _uiState.asStateFlow()


}

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
        artTitle = "",
        artist = "",
        artImgUrl = ""
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
        artTitle = "",
        artist = "",
        artImgUrl = ""
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