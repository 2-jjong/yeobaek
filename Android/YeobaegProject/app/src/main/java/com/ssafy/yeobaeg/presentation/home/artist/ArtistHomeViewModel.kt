package com.ssafy.yeobaeg.presentation.home.artist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.SlotStatus
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@Immutable
data class ArtistHomeState(
    val name: String = "김아티스트",
    val profileImageUrl: String? = null,
    val worksCount: Int = 12,
    val activeExhibitions: Int = 3,
    val artworks: List<ArtistArtwork> = demoArtworks
)

@Immutable
data class ArtistArtwork(
    val id: Long,
    val title: String,
    val thumbnailUrl: String,
    val priceWon: String,           // "￦450,000" 등 표시용
    val likes: Int,
    val placeName: String? = null,  // 전시 중일 때만 노출
    val status: SlotStatus
)


// 실제로는 @HiltViewModel 로 상태 만들면 됨
@HiltViewModel
class ArtistHomeViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {

    private var _uiState = MutableStateFlow(ArtistHomeState())
    val uiState = _uiState.asStateFlow()

    fun loadArtistData() {

    }

    fun navigateToRegisterArtwork() = viewModelScope.launch {
        navigator.navigate(FullScreen.RegisterArtwork)
    }
}

// 샘플 데이터
private val demoArtworks = listOf(
    ArtistArtwork(
        id = 1L,
        title = "Urban Dreams",
        thumbnailUrl = "https://picsum.photos/seed/a1/300/200",
        priceWon = "￦450,000",
        likes = 45,
        placeName = "Brew & Canvas",
        status = SlotStatus.EXHIBITING
    ),
    ArtistArtwork(
        id = 2L,
        title = "Morning Light",
        thumbnailUrl = "https://picsum.photos/seed/a2/300/200",
        priceWon = "￦320,000",
        likes = 28,
        placeName = null,
        status = SlotStatus.AVAILABLE
    ),
    ArtistArtwork(
        id = 3L,
        title = "Coffee Stories",
        thumbnailUrl = "https://picsum.photos/seed/a3/300/200",
        priceWon = "￦280,000",
        likes = 12,
        placeName = "The Art Spot",
        status = SlotStatus.RESERVED
    )
)