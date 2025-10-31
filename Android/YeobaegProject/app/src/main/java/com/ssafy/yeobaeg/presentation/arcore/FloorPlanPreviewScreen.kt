// FloorPlanPreviewScreen.kt (전체)
package com.ssafy.yeobaeg.presentation.arcore

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.ssafy.yeobaeg.domain.model.DisplayWall
import com.ssafy.yeobaeg.domain.model.Vec2
import com.ssafy.yeobaeg.domain.model.WallStatus
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@SuppressLint("DefaultLocale")
@Composable
fun FloorPlanPreview(
    initialWalls: List<DisplayWall>,
    modifier: Modifier = Modifier
) {
    // 화면 내부에서만 상태 관리
    val wallsState = remember(initialWalls) { initialWalls.toMutableStateList() }
    val walls: List<DisplayWall> = wallsState

    if (walls.isEmpty()) {
        Box(modifier = modifier) {
            Text("도면 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    // 탭 히트테스트용: 화면 좌표 선분 캐시
    val mappedSegmentsState = remember { mutableStateOf(emptyList<Pair<Offset, Offset>>()) }

    BoxWithConstraints(modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val padOuter = 16.dp
        val padOuterPx = with(density) { padOuter.toPx() }

        // ---- 0) 도면 바운딩 박스(월드 좌표 기준) ----
        val allPoints = walls.flatMap { listOf(it.start, it.end) }
        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }
        val contentW = (maxX - minX).coerceAtLeast(1e-3f)
        val contentH = (maxY - minY).coerceAtLeast(1e-3f)

        // ---- 1) 부모 제약(픽셀) ----
        val parentWpx = with(density) { maxWidth.toPx() }
        val parentHpx = with(density) { maxHeight.toPx() }

        // ---- 2) 스케일 및 실제 캔버스 크기(픽셀) ----
        val s = min(
            (parentWpx - 2 * padOuterPx) / contentW,
            (parentHpx - 2 * padOuterPx) / contentH
        ).coerceAtLeast(1f)

        val canvasWpx = (contentW * s + 2 * padOuterPx).coerceAtMost(parentWpx)
        val canvasHpx = (contentH * s + 2 * padOuterPx).coerceAtMost(parentHpx)

        val canvasW = with(density) { canvasWpx.toDp() }
        val canvasH = with(density) { canvasHpx.toDp() }

        // ✅ 정확한 크기의 박스를 중앙에 두고, 그 안에서만 그리기
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(canvasW, canvasH)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
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
                    // 캔버스 내부 좌표(중앙 정렬)
                    val cx = size.width * 0.5f
                    val cy = size.height * 0.5f
                    val originX = cx - canvasWpx * 0.5f
                    val originY = cy - canvasHpx * 0.5f

                    // 월드(x,y)를 화면좌표로 매핑
                    fun map(x: Float, y: Float): Offset {
                        val mx = originX + (x - minX) * s + padOuterPx
                        val my = originY + (y - minY) * s + padOuterPx
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

                    // ---- 4) 코너(초록 점) ----
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

/* ====== 프리뷰들 ====== */
@Preview(showBackground = true, name = "사각형 방")
@Composable
private fun PreviewFloorPlan_Rectangle() {
    val walls = listOf(
        DisplayWall(
            id = null, wName = "벽1",
            start = Vec2(0f, 0f), end = Vec2(5f, 0f),
            status = WallStatus.POSSIBLE
        ),
        DisplayWall(
            id = null, wName = "벽2",
            start = Vec2(5f, 0f), end = Vec2(5f, 4f),
            status = WallStatus.IN_USE
        ),
        DisplayWall(
            id = null, wName = "벽3",
            start = Vec2(5f, 4f), end = Vec2(0f, 4f),
            status = WallStatus.POSSIBLE
        ),
        DisplayWall(
            id = null, wName = "벽4",
            start = Vec2(0f, 4f), end = Vec2(0f, 0f),
            status = WallStatus.NOT_POSSIBLE
        )
    )
    FloorPlanPreview(initialWalls = walls, modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true, name = "L자형 방")
@Composable
private fun PreviewFloorPlan_LShape() {
    val walls = listOf(
        DisplayWall(
            id = null, wName = "벽1",
            start = Vec2(0f, 0f), end = Vec2(6f, 0f),
            status = WallStatus.POSSIBLE
        ),
        DisplayWall(
            id = null, wName = "벽2",
            start = Vec2(6f, 0f), end = Vec2(6f, 3f),
            status = WallStatus.IN_USE
        ),
        DisplayWall(
            id = null, wName = "벽3",
            start = Vec2(6f, 3f), end = Vec2(3f, 3f),
            status = WallStatus.RESERVED
        ),
        DisplayWall(
            id = null, wName = "벽4",
            start = Vec2(3f, 3f), end = Vec2(3f, 5f),
            status = WallStatus.POSSIBLE
        ),
        DisplayWall(
            id = null, wName = "벽5",
            start = Vec2(3f, 5f), end = Vec2(0f, 5f),
            status = WallStatus.NOT_POSSIBLE
        ),
        DisplayWall(
            id = null, wName = "벽6",
            start = Vec2(0f, 5f), end = Vec2(0f, 0f),
            status = WallStatus.POSSIBLE
        )
    )
    FloorPlanPreview(initialWalls = walls, modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true, name = "갤러리 공간")
@Composable
private fun PreviewFloorPlan_Gallery() {
    val walls = listOf(
        DisplayWall(
            id = null, wName = "벽1",
            start = Vec2(0f, 0f), end = Vec2(8f, 0f),
            status = WallStatus.POSSIBLE
        ),
        DisplayWall(
            id = null, wName = "벽2",
            start = Vec2(8f, 0f), end = Vec2(8f, 6f),
            status = WallStatus.IN_USE
        ),
        DisplayWall(
            id = null, wName = "벽3",
            start = Vec2(8f, 6f), end = Vec2(0f, 6f),
            status = WallStatus.RESERVED
        ),
        DisplayWall(
            id = null, wName = "벽4",
            start = Vec2(0f, 6f), end = Vec2(0f, 0f),
            status = WallStatus.POSSIBLE
        )
    )
    FloorPlanPreview(initialWalls = walls, modifier = Modifier.fillMaxWidth())
}
