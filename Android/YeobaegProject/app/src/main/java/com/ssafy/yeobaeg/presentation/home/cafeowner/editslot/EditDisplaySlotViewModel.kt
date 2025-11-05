package com.ssafy.yeobaeg.presentation.home.cafeowner.editslot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditDisplaySlotViewModel @Inject constructor(
    private val cafeRepository: CafeRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditSlotState())
    val uiState  = _uiState.asStateFlow()

    fun update(block: EditSlotState.() -> EditSlotState) {
        _uiState.value = _uiState.value.block()
    }

    fun registerSlot() {
        navigateBack()
    }

    fun onBackPressed() = viewModelScope.launch {
        navigator.navigateBack()
    }

    private fun navigateBack() = viewModelScope.launch {
        Log.d("editdisplayslotviewmodel", "")
        navigator.navigateBack()
    }
}

data class EditSlotState(
    val startAtMillis: Long? = null,
    val endAtMillis: Long? = null,
    val feePercent: Int? = null,
    val note: String = ""
) {
    val isValid: Boolean
        get() = startAtMillis != null && endAtMillis != null && feePercent != null && note.length <= 500
}
