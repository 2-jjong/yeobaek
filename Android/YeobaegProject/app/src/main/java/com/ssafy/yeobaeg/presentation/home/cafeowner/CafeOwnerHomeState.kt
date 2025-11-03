package com.ssafy.yeobaeg.presentation.home.cafeowner

import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.DisplayWall


enum class CafeOwnerTab(val label: String) {
    PLAN("공간 도면"),
    STATUS("전시 현황")
}

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
    val displaySlots: List<DisplaySlot> = dummyDisplaySlots,
    val displayWalls: List<DisplayWall> = dummyDisplayWalls,
    val selectedTab: CafeOwnerTab = CafeOwnerTab.PLAN
)
