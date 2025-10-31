// FloorPlanPreviewScreen.kt (전체)
package com.ssafy.yeobaeg.presentation.arcore

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ssafy.yeobaeg.domain.model.DisplayWall
import com.ssafy.yeobaeg.domain.model.WallStatus
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import androidx.core.graphics.toColorInt

/**
 * - ViewModel에 의존하지 않고, 전달받은 초기 walls를 내부 상태로 복사하여 관리합니다.
 * - 탭: 가능 <-> 불가능 토글
 * - 롱탭: 전시중 <-> 가능 토글
 */
@SuppressLint("DefaultLocale")
@Composable
fun FloorPlanPreview(
    initialWalls: List<DisplayWall>,
    modifier: Modifier = Modifier
) {
    // 전달받은 초기 데이터로 내부 상태 구성(프리뷰 단독 관리)
    val wallsState = remember(initialWalls) { initialWalls.toMutableStateList() }
    val walls = wallsState as List<DisplayWall>

    Box(modifier.fillMaxSize()) {
        if (walls.isEmpty()) {
            Text("도면 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        // 탭 히트테스트용: 화면 좌표 선분 캐시
        val mappedSegmentsState = remember { mutableStateOf(emptyList<Pair<Offset, Offset>>()) }

        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(walls) {
                        detectTapGestures(
                            onTap = { tap ->
                                val (idx, dist) = findNearestSegment(mappedSegmentsState.value, tap)
                                if (idx != null && dist <= 30f) toggleWallPossible(wallsState, idx)
                            },
                            onLongPress = { tap ->
                                val (idx, dist) = findNearestSegment(mappedSegmentsState.value, tap)
                                if (idx != null && dist <= 30f) toggleWallInUse(wallsState, idx)
                            }
                        )
                    }
            ) {
                // ---- 0) bounds 계산 ----
                val allPoints = walls.flatMap { listOf(it.start, it.end) }
                var minX = Float.POSITIVE_INFINITY
                var maxX = Float.NEGATIVE_INFINITY
                var minY = Float.POSITIVE_INFINITY
                var maxY = Float.NEGATIVE_INFINITY
                for (p in allPoints) {
                    minX = min(minX, p.x); maxX = max(maxX, p.x)
                    minY = min(minY, p.y); maxY = max(maxY, p.y)
                }

                val w = size.width
                val h = size.height
                val pad = 40f
                val sx = (w - pad * 2f) / (maxX - minX).coerceAtLeast(1e-3f)
                val sy = (h - pad * 2f) / (maxY - minY).coerceAtLeast(1e-3f)
                val s = min(sx, sy)

                fun map(x: Float, y: Float): Offset {
                    val mx = (x - minX) * s + pad
                    val my = (y - minY) * s + pad
                    // 상단이 +Y
                    return Offset(mx, my)
                }

                // ---- 1) 외곽 가이드 ----
                val orderedVertices = walls.map { it.start } + listOf(walls.last().end)
                val mappedVertices = orderedVertices.map { map(it.x, it.y) }
                if (mappedVertices.size >= 3) {
                    val outline = Path().apply {
                        val p0 = mappedVertices.first()
                        moveTo(p0.x, p0.y)
                        for (i in 1 until mappedVertices.size) {
                            val pi = mappedVertices[i]
                            lineTo(pi.x, pi.y)
                        }
                        close()
                    }
                    drawPath(
                        path = outline,
                        color = Color(0xFFE5E7EB),
                        style = Stroke(width = 6f)
                    )
                }

                // ---- 2) 벽 선분 + 히트맵 ----
                val mappedSegments = ArrayList<Pair<Offset, Offset>>(walls.size)
                for (wall in walls) {
                    val a = map(wall.start.x, wall.start.y)
                    val b = map(wall.end.x, wall.end.y)
                    val (stroke, color) = when (wall.status) {
                        WallStatus.NOT_POSSIBLE -> 20f to Color(0xFFCBD5E1)   // 회색
                        WallStatus.POSSIBLE     -> 20f to Color(0xFF10B981)  // 초록
                        WallStatus.IN_USE       -> 20f to Color(0xFFEF4444)  // 빨강
                        WallStatus.RESERVED     -> 20f to Color(0xFFF59E0B)  // 주황
                    }
                    drawLine(color = color, start = a, end = b, strokeWidth = stroke)
                    mappedSegments += a to b
                }
                mappedSegmentsState.value = mappedSegments

                // ---- 3) 라벨(길이) ----
                fun signedArea(vertices: List<Offset>): Float {
                    var area = 0f
                    for (i in 0 until vertices.size - 1) {
                        val p = vertices[i]
                        val q = vertices[i + 1]
                        area += p.x * q.y - q.x * p.y
                    }
                    return area * 0.5f
                }
                val isCCW = mappedVertices.size >= 3 && signedArea(mappedVertices) > 0f

                val native = drawContext.canvas.nativeCanvas
                val textPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = "#334155".toColorInt()
                    textSize = max(24f, 36f)
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val wallStrokePx = 20f
                val marginPx = 8f
                val outwardOffsetPx = wallStrokePx * 0.5f + marginPx

                walls.forEach { wall ->
                    if (wall.status == WallStatus.NOT_POSSIBLE) return@forEach

                    val dx = wall.end.x - wall.start.x
                    val dy = wall.end.y - wall.start.y
                    val lengthM = sqrt(dx * dx + dy * dy)
                    if (!lengthM.isFinite() || lengthM <= 1e-6f) return@forEach

                    val midX = (wall.start.x + wall.end.x) * 0.5f
                    val midY = (wall.start.y + wall.end.y) * 0.5f
                    val mid = map(midX, midY)

                    val len = sqrt(dx * dx + dy * dy)
                    var nx =  dy / len
                    var ny = -dx / len
                    if (!isCCW) { nx = -nx; ny = -ny }

                    val labelPos = Offset(
                        mid.x + nx * outwardOffsetPx,
                        mid.y + ny * outwardOffsetPx
                    )

                    val text = if (lengthM < 1f) {
                        "${(lengthM * 100).toInt()} cm"
                    } else {
                        String.format("%.2f m", lengthM)
                    }
                    native.drawText(text, labelPos.x, labelPos.y, textPaint)
                }

                // ---- 4) 코너 표시 ----
                mappedVertices.dropLast(1).forEach { p ->
                    drawCircle(
                        Color(0xFF1B5E20),
                        radius = 15f,
                        center = p
                    )
                }
            }
        }
    }
}

/* ====== 내부 토글 로직 ====== */

private fun toggleWallPossible(walls: MutableList<DisplayWall>, index: Int) {
    if (index !in walls.indices) return
    val w = walls[index]
    val newStatus = when (w.status) {
        WallStatus.NOT_POSSIBLE -> WallStatus.POSSIBLE
        WallStatus.POSSIBLE, WallStatus.IN_USE, WallStatus.RESERVED -> WallStatus.NOT_POSSIBLE
    }
    walls[index] = w.copy(status = newStatus)
}

private fun toggleWallInUse(walls: MutableList<DisplayWall>, index: Int) {
    if (index !in walls.indices) return
    val w = walls[index]
    val newStatus = when (w.status) {
        WallStatus.IN_USE -> WallStatus.POSSIBLE
        else -> WallStatus.IN_USE
    }
    walls[index] = w.copy(status = newStatus)
}

/* ====== 탭 히트 테스트 (화면 좌표 선분들 대상) ====== */
private fun findNearestSegment(
    segments: List<Pair<Offset, Offset>>,
    tap: Offset
): Pair<Int?, Float> {
    var bestIdx: Int? = null
    var bestDist = Float.POSITIVE_INFINITY
    segments.forEachIndexed { idx, (a, b) ->
        val d = pointToSegmentDistance(tap, a, b)
        if (d < bestDist) {
            bestDist = d
            bestIdx = idx
        }
    }
    return bestIdx to bestDist
}

private fun pointToSegmentDistance(p: Offset, a: Offset, b: Offset): Float {
    val abx = b.x - a.x; val aby = b.y - a.y
    val apx = p.x - a.x; val apy = p.y - a.y
    val ab2 = abx * abx + aby * aby
    if (ab2 <= 1e-6f) return hypot(apx, apy)
    val t = ((apx * abx) + (apy * aby)) / ab2
    val tt = t.coerceIn(0f, 1f)
    val cx = a.x + abx * tt; val cy = a.y + aby * tt
    return hypot(p.x - cx, p.y - cy)
}
