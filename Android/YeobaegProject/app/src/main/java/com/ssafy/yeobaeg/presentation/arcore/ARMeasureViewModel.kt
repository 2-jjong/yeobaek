// ARMeasureViewModel.kt (전체)
package com.ssafy.yeobaeg.presentation.arcore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ar.core.Plane
import com.google.ar.core.Pose
import com.ssafy.yeobaeg.domain.model.DisplayWall
import com.ssafy.yeobaeg.domain.model.Vec2
import com.ssafy.yeobaeg.domain.model.WallStatus
import com.ssafy.yeobaeg.navigation.BottomNavScreen
import com.ssafy.yeobaeg.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.sceneview.math.Position
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class ARMeasureViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {

    data class ARMeasureState(
        val planePose: Pose? = null,
        val cornersWorld: List<Position> = emptyList(),
        val walls: List<DisplayWall> = emptyList(),  // ← 프리뷰가 보는 데이터
        val isBuilding: Boolean = false
    )

    private val _uiState = MutableStateFlow(ARMeasureState())
    val uiState: StateFlow<ARMeasureState> = _uiState

    /* ---------------------------
     * 측정 포인트 관리
     * --------------------------- */
    fun addCorner(world: Position, plane: Plane?) {
        _uiState.update { cur ->
            val planePose = cur.planePose ?: plane?.centerPose
            cur.copy(
                planePose = planePose ?: cur.planePose,
                cornersWorld = cur.cornersWorld + world
            )
        }
    }

    fun undoCorner() = _uiState.update { cur ->
        if (cur.cornersWorld.isEmpty()) cur
        else cur.copy(cornersWorld = cur.cornersWorld.dropLast(1))
    }

    fun clearAll() = _uiState.update {
        it.copy(planePose = null, cornersWorld = emptyList())
    }

    /* ---------------------------
     * 도면 빌드 & 네비게이션
     * --------------------------- */
    fun buildAndNavigate() {
        val s = _uiState.value
        val planePose = s.planePose ?: return
        if (s.cornersWorld.size < 3) return

        _uiState.update { it.copy(isBuilding = true) }

        viewModelScope.launch {
            // 월드 → 평면 로컬 XZ, 각도 스냅, 근폐합
            val local = worldToPlaneLocalXZ(planePose, s.cornersWorld)
            val snapped = snapPolylineAngles(local, 45f, 0f)
            val closed = closeIfNear(snapped)

            // 로컬 좌표들을 '연속 세그먼트'로 쪼개 DisplayWall 리스트 생성
            val walls = buildWallsFromPolyline(closed)

            _uiState.update { it.copy(walls = walls, isBuilding = false) }

            navigator.navigate(BottomNavScreen.SpaceScanPreview)
        }
    }

    // polyline -> segments
    private fun buildWallsFromPolyline(poly: List<Vec2>): List<DisplayWall> {
        if (poly.size < 2) return emptyList()
        val result = ArrayList<DisplayWall>(poly.size)
        for (i in 0 until poly.size) {
            val a = poly[i]
            val b = poly[(i + 1) % poly.size] // 마지막-첫번째 연결 포함
            val dx = b.x - a.x
            val dy = b.y - a.y
            val len = sqrt(dx * dx + dy * dy)
            if (!len.isFinite() || len <= 1e-6f) continue // 0cm 세그먼트 제거

            result += DisplayWall(
                start = a,
                end = b,
                wName = "벽 ${i + 1}",
                status = WallStatus.POSSIBLE // 기본값
            )
        }
        return result
    }

    /* ---------------------------
     * 벽 상태 토글/설정
     * - onTap: 가능 <-> 불가능
     * - onLongPress: 전시 중 <-> 가능
     * --------------------------- */
    fun toggleWallPossible(index: Int) {
        val cur = _uiState.value.walls
        if (index !in cur.indices) return
        val next = cur.toMutableList()
        val w = next[index]
        val newStatus = when (w.status) {
            WallStatus.NOT_POSSIBLE -> WallStatus.POSSIBLE
            WallStatus.POSSIBLE, WallStatus.IN_USE, WallStatus.RESERVED -> WallStatus.NOT_POSSIBLE
        }
        next[index] = w.copy(status = newStatus)
        _uiState.update { it.copy(walls = next) }
    }

    fun toggleWallInUse(index: Int) {
        val cur = _uiState.value.walls
        if (index !in cur.indices) return
        val next = cur.toMutableList()
        val w = next[index]
        val newStatus = when (w.status) {
            WallStatus.IN_USE -> WallStatus.POSSIBLE
            else -> WallStatus.IN_USE
        }
        next[index] = w.copy(status = newStatus)
        _uiState.update { it.copy(walls = next) }
    }

    fun setWallStatus(index: Int, status: WallStatus) {
        val cur = _uiState.value.walls
        if (index !in cur.indices) return
        val next = cur.toMutableList()
        next[index] = next[index].copy(status = status)
        _uiState.update { it.copy(walls = next) }
    }

    /* ---------------------------
     * 좌표/스냅 유틸
     * --------------------------- */
    private fun worldToPlaneLocalXZ(plane: Pose, pts: List<Position>): List<Vec2> =
        pts.map { p ->
            val out = plane.inverse().transformPoint(floatArrayOf(p.x, p.y, p.z))
            Vec2(out[0], out[2])
        }

    private fun snapPolylineAngles(poly: List<Vec2>, stepDeg: Float, tolDeg: Float): List<Vec2> {
        if (poly.size < 2) return poly
        val step = Math.toRadians(stepDeg.toDouble())
        val tol  = Math.toRadians(tolDeg.toDouble())

        fun nearest(a: Double): Double {
            val k = round(a / step)
            val target = k * step
            return if (abs(a - target) <= tol) target else a
        }

        val res = ArrayList<Vec2>(poly.size)
        res += poly.first()
        for (i in 1 until poly.size) {
            val prev = res.last()
            val cur  = poly[i]
            val dx = (cur.x - prev.x).toDouble()
            val dy = (cur.y - prev.y).toDouble()
            val len = hypot(dx, dy).coerceAtLeast(1e-6)
            val ang = atan2(dy, dx)
            val sna = nearest(ang)
            val nx = cos(sna)
            val ny = sin(sna)
            res += Vec2(prev.x + (nx * len).toFloat(), prev.y + (ny * len).toFloat())
        }
        return res
    }

    private fun closeIfNear(poly: List<Vec2>, tol: Float = 0.05f): List<Vec2> {
        if (poly.size < 3) return poly
        val a = poly.first()
        val b = poly.last()
        val dx = a.x - b.x
        val dy = a.y - b.y
        return if (sqrt(dx*dx + dy*dy) <= tol) poly.dropLast(1) + a else poly
    }
}
