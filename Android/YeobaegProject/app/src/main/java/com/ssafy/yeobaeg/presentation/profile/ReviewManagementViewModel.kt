package com.ssafy.yeobaeg.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.profile.ReviewStats
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun navigateBack() = viewModelScope.launch {
        navigator.navigateBack()
    }

}