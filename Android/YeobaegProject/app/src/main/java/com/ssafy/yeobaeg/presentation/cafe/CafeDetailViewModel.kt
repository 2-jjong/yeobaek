package com.ssafy.yeobaeg.presentation.cafe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.cafe.CafeDetail
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.FullScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CafeDetailUiState(
    val cafeDetail: CafeDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: CafeDetailTab = CafeDetailTab.EXHIBITION
)

enum class CafeDetailTab {
    EXHIBITION,  // 전시
    INFO         // 정보
}

@HiltViewModel
class CafeDetailViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val navigator: Navigator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CafeDetailUiState())
    val uiState: StateFlow<CafeDetailUiState> = _uiState.asStateFlow()
    
    /**
     * 카페 상세 정보 로드
     */
    fun loadCafeDetail(cafeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            cafeRepository.getCafeDetail(cafeId)
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(
                            cafeDetail = detail,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("CafeDetailViewModel", "Failed to load cafe detail", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "카페 정보를 불러올 수 없습니다"
                        )
                    }
                }
        }
    }
    
    /**
     * 탭 변경
     */
    fun selectTab(tab: CafeDetailTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
    
    /**
     * 북마크 토글
     */
    fun toggleBookmark() {
        // TODO: 북마크 API 연동
        _uiState.update {
            it.copy(
                cafeDetail = it.cafeDetail?.copy(
                    isBookmarked = !it.cafeDetail.isBookmarked
                )
            )
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

    /**
     * 전시 캘린더로 이동
     */
    fun navigateToExhibitionCalendar(slotId: Long, slotName: String) {
        viewModelScope.launch {
            navigator.navigate(
                route = FullScreen.ExhibitionCalendar(
                    slotId = slotId,
                    slotName = slotName
                )
            )
        }
    }

    /**
     * 에러 메시지 클리어
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

