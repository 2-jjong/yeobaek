// RegisterCafeViewModel.kt
package com.ssafy.yeobaeg.presentation.home.cafeowner.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val STEP1 = 1
const val STEP2 = 2
const val TOTAL_STEPS = 2

@HiltViewModel
class RegisterCafeViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterCafeState())
    val uiState: StateFlow<RegisterCafeState> = _uiState.asStateFlow()

    fun update(block: RegisterCafeState.() -> RegisterCafeState) {
        _uiState.value = _uiState.value.block()
    }

    fun next() {
        if(_uiState.value.step == TOTAL_STEPS) {
            navigateToCafeOwnerHome()
        }
        update { copy(step = if (step == STEP1) STEP2 else step) }
    }
    fun prev() = update { copy(step = if (step == STEP2) STEP1 else step) }

    fun toggleStyle(style: CafeInteriorStyle) = update {
        val set = selectedStyles.toMutableSet()
        if (style in set) set.remove(style) else if (set.size < 3) set.add(style)
        copy(selectedStyles = set)
    }

    fun selectVisitorsBand(band: VisitorsBand) = update { copy(visitorsBand = band) }

    fun addImage(uri: String) = update { if (images.size >= 10) this else copy(images = images + uri) }
    fun removeImage(uri: String) = update { copy(images = images - uri) }

    fun navigateToCafeOwnerHome() = viewModelScope.launch {
        navigator.navigate(BottomNavScreen.Home)
    }
}

data class RegisterCafeState(
    val step: Int = STEP1,
    val name: String = "",
    val address: String = "",
    val description: String = "",
    val phone: String = "",
    val openTime: String = "",
    val closeTime: String = "",
    val images: Set<String> = emptySet(),
    val selectedStyles: Set<CafeInteriorStyle> = emptySet(),
    val visitorsBand: VisitorsBand? = null
) {
    val isStep1Valid get() =
        name.isNotBlank() && address.isNotBlank() && phone.isNotBlank() &&
                openTime.isNotBlank() && closeTime.isNotBlank() && images.size in 3..10
    val isStep2Valid get() =
        selectedStyles.isNotEmpty() && selectedStyles.size <= 3 && visitorsBand != null
}

enum class CafeInteriorStyle(val label: String) {
    MODERN("모던"), VINTAGE("빈티지"), MINIMAL("미니멀"), INDUSTRIAL("인더스트리얼"),
    CLASSIC("클래식"), RETRO("레트로"), SCANDINAVIAN("스칸디나비아"), NATURAL("네추럴");
}

enum class VisitorsBand(val label: String) {
    UNDER_50("~ 50명"), V50_100("50 ~ 100명"), V100_200("100 ~ 200명"),
    V200_300("200 ~ 300명"), V300_500("300 ~ 500명"), OVER_500("500명 이상");
}
