package com.ssafy.yeobaeg.presentation.profile

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.profile.ReviewStats
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewStatsUiState(
    val isLoading: Boolean = false,
    val reviewStats: ReviewStats? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ReviewManagementViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewStatsUiState())
    val uiState: StateFlow<ReviewStatsUiState> = _uiState.asStateFlow()

    fun loadReviewStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getReviewStatistics(0)
                .onSuccess { stats ->
                    Log.d(TAG, "리뷰 통계 조회 성공: $stats")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reviewStats = stats,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "리뷰 통계 조회 실패: ${error.message}", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "리뷰 통계 불러오기 실패"
                        )
                    }
                }
        }
    }

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

}