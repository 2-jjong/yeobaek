package com.ssafy.yeobaeg.presentation.arcore.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yeobaeg.domain.model.DisplayWall
import com.ssafy.yeobaeg.domain.model.WallStatus
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WallEditStep { SelectWall, MeasureHeight }

data class WallEditUiState(
    val step: WallEditStep = WallEditStep.SelectWall,
    val walls: List<DisplayWall> = emptyList(),
    val selectedWallIds: Set<Long> = emptySet(),
    val targetIds: List<Long> = emptyList(),
    val currentWallId: Long? = null,
) {
    val currentIndex: Int
        get() = currentWallId?.let { id -> targetIds.indexOf(id) } ?: -1

    val allMeasured: Boolean
        get() {
            if (targetIds.isEmpty()) return false
            return targetIds.all { id ->
                val wall = walls.firstOrNull { it.id == id }
                wall?.height != null && wall.height > 0f
            }
        }

    fun currentWallOrNull(): DisplayWall? =
        currentWallId?.let { id -> walls.firstOrNull { it.id == id } }
}

@HiltViewModel
class WallEditViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(WallEditUiState())
    val uiState: StateFlow<WallEditUiState> = _uiState.asStateFlow()

    fun setInitialWalls(walls: List<DisplayWall>) {
        _uiState.update { it.copy(walls = walls) }
    }

    fun onWallTapped(wallId: Long) {
        _uiState.update { s ->
            val idx = s.walls.indexOfFirst { it.id == wallId }
            if (idx < 0) return@update s

            val w = s.walls[idx]
            val toggled = when (w.status) {
                WallStatus.NOT_POSSIBLE -> WallStatus.POSSIBLE
                WallStatus.POSSIBLE, WallStatus.IN_USE, WallStatus.RESERVED -> WallStatus.NOT_POSSIBLE
            }

            val nextWalls = s.walls.toMutableList().also { it[idx] = w.copy(status = toggled) }
            val nextSelected = s.selectedWallIds.toMutableSet().also {
                if (toggled != WallStatus.NOT_POSSIBLE) it.add(wallId) else it.remove(wallId)
            }
            s.copy(walls = nextWalls, selectedWallIds = nextSelected)
        }
    }

    fun updateWallName(wallId: Long, newName: String) {
        _uiState.update { s ->
            val idx = s.walls.indexOfFirst { it.id == wallId }
            if (idx < 0) s else {
                val nextList = s.walls.toMutableList()
                nextList[idx] = nextList[idx].copy(wName = newName)
                s.copy(walls = nextList)
            }
        }
    }

    fun selectWall(wallId: Long) {
        _uiState.update { s ->
            val w = s.walls.firstOrNull { it.id == wallId } ?: return@update s
            if (w.status == WallStatus.NOT_POSSIBLE) return@update s
            s.copy(currentWallId = wallId)
        }
    }

    fun proceedToMeasure() {
        _uiState.update { s ->
            val selectableIds = s.walls
                .filter { it.status != WallStatus.NOT_POSSIBLE }
                .map { it.id }

            val targets = if (s.selectedWallIds.isNotEmpty())
                s.selectedWallIds.filter { it in selectableIds }
            else
                selectableIds

            s.copy(
                step = WallEditStep.MeasureHeight,
                targetIds = targets,
                currentWallId = targets.firstOrNull()
            )
        }
    }

    /** ✅ 벽의 height 속성 업데이트 */
    fun saveMeasuredHeight(wallId: Long, heightM: Float) {
        _uiState.update { s ->
            val idx = s.walls.indexOfFirst { it.id == wallId }
            if (idx < 0) return@update s

            val nextWalls = s.walls.toMutableList()
            nextWalls[idx] = nextWalls[idx].copy(height = heightM)
            s.copy(walls = nextWalls)
        }
    }

    /** ✅ 다음 측정 안된 벽으로 이동 */
    fun goNext() {
        _uiState.update { s ->
            if (s.targetIds.isEmpty()) return@update s

            val currentIdx = s.currentIndex
            if (currentIdx < 0) {
                // 현재 선택 없음 → 첫 번째 측정 안된 벽으로
                val firstUnmeasured = s.targetIds.firstOrNull { id ->
                    val wall = s.walls.firstOrNull { it.id == id }
                    wall?.height == null || wall.height <= 0f
                }
                return@update s.copy(currentWallId = firstUnmeasured ?: s.targetIds.firstOrNull())
            }

            // 현재 위치 다음부터 끝까지 검색
            var i = currentIdx + 1
            while (i < s.targetIds.size) {
                val id = s.targetIds[i]
                val w = s.walls.firstOrNull { it.id == id }
                val selectable = w?.status != WallStatus.NOT_POSSIBLE
                val unmeasured = w?.height == null || w.height <= 0f

                if (selectable && unmeasured) {
                    return@update s.copy(currentWallId = id)
                }
                i++
            }

            // 뒤에 없으면 처음부터 현재 위치까지 검색 (순환)
            i = 0
            while (i < currentIdx) {
                val id = s.targetIds[i]
                val w = s.walls.firstOrNull { it.id == id }
                val selectable = w?.status != WallStatus.NOT_POSSIBLE
                val unmeasured = w?.height == null || w.height <= 0f

                if (selectable && unmeasured) {
                    return@update s.copy(currentWallId = id)
                }
                i++
            }

            // 모두 측정됨 → 현재 위치 유지
            s
        }
    }

    fun prevOrBackToSelect() {
        if (_uiState.value.step == WallEditStep.SelectWall) {
            navigateToHome()
            return
        }
        _uiState.update { s ->
            val idx = s.currentIndex
            if (idx <= 0) {
                s.copy(step = WallEditStep.SelectWall, currentWallId = null)
            } else {
                var i = idx - 1
                while (i >= 0) {
                    val id = s.targetIds[i]
                    val w = s.walls.firstOrNull { it.id == id }
                    if (w?.status != WallStatus.NOT_POSSIBLE) {
                        return@update s.copy(currentWallId = id)
                    }
                    i--
                }
                s.copy(step = WallEditStep.SelectWall, currentWallId = null)
            }
        }
    }

    fun navigateToHome() = viewModelScope.launch {
        navigator.navigateAndClearBackStack(BottomNavScreen.Home)
    }

    fun registerSlot() {
        // _ui.value.walls 등록

        viewModelScope.launch {
            navigator.navigateAndClearBackStack(BottomNavScreen.Home)
        }
    }
}