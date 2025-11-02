package com.ssafy.yeobaeg.presentation.arcore

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import kotlin.math.min
import kotlin.math.sqrt

/* ====== 상수 ====== */
private const val HIT_THRESHOLD_PX = 30f
private const val WALL_STROKE_PX   = 20f
private const val LABEL_MARGIN_PX  = 8f
private const val OUTLINE_STROKE_PX = 6f

@SuppressLint("DefaultLocale")
@Composable
fun FloorPlanPreview(
    initialWalls: List<DisplayWall>,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    // 화면 내부 상태(이 컴포넌트 국한)
    val wallsState = remember(initialWalls) { initialWalls.toMutableStateList() }
    val walls: List<DisplayWall> = wallsState

    if (walls.isEmpty()) {
        Box(modifier = modifier) {
            Text("도면 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    // 탭 히트 테스트용 화면 좌표 캐시
    val mappedSegmentsState = remember { mutableStateOf(emptyList<Pair<Offset, Offset>>()) }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val padOuter = 16.dp
        val padOuterPx = with(density) { padOuter.toPx() }

        // ---- 0) 도면 바운딩(월드 좌표) ----
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

        // ---- 2) 스케일/캔버스 크기 ----
        val s = min(
            (parentWpx - 2 * padOuterPx) / contentW,
            (parentHpx - 2 * padOuterPx) / contentH
        ).coerceAtLeast(1f)

        val canvasWpx = (contentW * s + 2 * padOuterPx).coerceAtMost(parentWpx)
        val canvasHpx = (contentH * s + 2 * padOuterPx).coerceAtMost(parentHpx)
        val canvasW = with(density) { canvasWpx.toDp() }
        val canvasH = with(density) { canvasHpx.toDp() }

        // 텍스트 페인트 캐싱
        val textPaint = remember {
            android.graphics.Paint().apply {
                isAntiAlias = true
                color = "#334155".toColorInt()
                textSize = 36f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                textAlign = android.graphics.Paint.Align.CENTER
            }
        }

        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(canvasW, canvasH)
                .align(Alignment.Center)
        ) {
            // readOnly 면 제스처 제거
            val canvasModifier =
                if (readOnly) {
                    Modifier.matchParentSize()
                } else {
                    Modifier
                        .matchParentSize()
                        .pointerInput(walls) {
                            detectTapGestures(
                                onTap = { tap ->
                                    val (idx, dist) = findNearestSegment(mappedSegmentsState.value, tap)
                                    if (idx != null && dist <= HIT_THRESHOLD_PX) toggleWallPossible(wallsState, idx)
                                },
                                onLongPress = { tap ->
                                    val (idx, dist) = findNearestSegment(mappedSegmentsState.value, tap)
                                    if (idx != null && dist <= HIT_THRESHOLD_PX) toggleWallInUse(wallsState, idx)
                                }
                            )
                        }
                }

            Canvas(modifier = canvasModifier) {
                // 월드(x,y) → 캔버스 좌표
                fun map(x: Float, y: Float): Offset {
                    val mx = (x - minX) * s + padOuterPx
                    val my = (y - minY) * s + padOuterPx
                    return Offset(mx, my)
                }

                // ---- 외곽 가이드 ----
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
                        style = Stroke(width = OUTLINE_STROKE_PX)
                    )
                }

                // ---- 벽 렌더 + 히트맵 ----
                val mappedSegments = if (!readOnly) ArrayList<Pair<Offset, Offset>>(walls.size) else null
                walls.forEach { wall ->
                    val a = map(wall.start.x, wall.start.y)
                    val b = map(wall.end.x, wall.end.y)
                    val (stroke, color) = wallStyle(wall.status)
                    drawLine(color = color, start = a, end = b, strokeWidth = stroke)
                    mappedSegments?.add(a to b)
                }
                if (!readOnly) {
                    mappedSegmentsState.value = mappedSegments ?: emptyList()
                }

                // ---- 라벨(길이) ----
                val ccw = isCCW(mappedVertices)
                val outwardOffsetPx = WALL_STROKE_PX * 0.5f + LABEL_MARGIN_PX

                val native = drawContext.canvas.nativeCanvas
                walls.forEach { wall ->
                    if (wall.status == WallStatus.NOT_POSSIBLE) return@forEach

                    val dx = wall.end.x - wall.start.x
                    val dy = wall.end.y - wall.start.y
                    val len = sqrt(dx * dx + dy * dy)       // == lengthM
                    if (!len.isFinite() || len <= 1e-6f) return@forEach

                    val midX = (wall.start.x + wall.end.x) * 0.5f
                    val midY = (wall.start.y + wall.end.y) * 0.5f
                    val mid = map(midX, midY)

                    var nx =  dy / len
                    var ny = -dx / len
                    if (!ccw) { nx = -nx; ny = -ny }

                    val labelPos = Offset(
                        mid.x + nx * outwardOffsetPx,
                        mid.y + ny * outwardOffsetPx
                    )

                    val text = if (len < 1f) "${(len * 100).toInt()} cm" else String.format("%.2f m", len)
                    native.drawText(text, labelPos.x, labelPos.y, textPaint)
                }

                // ---- 코너(초록 점) ----
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

/* ====== 스타일/유틸 ====== */
private fun wallStyle(status: WallStatus): Pair<Float, Color> = when (status) {
    WallStatus.NOT_POSSIBLE -> WALL_STROKE_PX to Color(0xFFCBD5E1)
    WallStatus.POSSIBLE     -> WALL_STROKE_PX to Color(0xFF10B981)
    WallStatus.IN_USE       -> WALL_STROKE_PX to Color(0xFFEF4444)
    WallStatus.RESERVED     -> WALL_STROKE_PX to Color(0xFFF59E0B)
}

private fun isCCW(vertices: List<Offset>): Boolean {
    if (vertices.size < 3) return true
    var area2 = 0f
    for (i in 0 until vertices.size - 1) {
        val p = vertices[i]; val q = vertices[i + 1]
        area2 += p.x * q.y - q.x * p.y
    }
    return area2 > 0f
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

/* ====== 탭 히트 테스트 ====== */
private fun findNearestSegment(
    segments: List<Pair<Offset, Offset>>,
    tap: Offset
): Pair<Int?, Float> {
    var bestIdx: Int? = null
    var bestDist = Float.POSITIVE_INFINITY
    segments.forEachIndexed { idx, (a, b) ->
        val d = pointToSegmentDistance(tap, a, b)
        if (d < bestDist) { bestDist = d; bestIdx = idx }
    }
    return bestIdx to bestDist
}

private fun pointToSegmentDistance(p: Offset, a: Offset, b: Offset): Float {
    val abx = b.x - a.x
    val aby = b.y - a.y
    val apx = p.x - a.x
    val apy = p.y - a.y
    val ab2 = abx * abx + aby * aby
    if (ab2 <= 1e-6f) return hypot(apx, apy)
    val t = ((apx * abx) + (apy * aby)) / ab2
    val tt = t.coerceIn(0f, 1f)
    val cx = a.x + abx * tt
    val cy = a.y + aby * tt
    return hypot(p.x - cx, p.y - cy)
}

/* ====== 프리뷰 ====== */
@Preview(showBackground = true, name = "수정 가능 (기본)")
@Composable
private fun PreviewFloorPlan_Edit() {
    val walls = listOf(
        DisplayWall(id = null, wName = "벽1", start = Vec2(0f, 0f), end = Vec2(5f, 0f), status = WallStatus.POSSIBLE),
        DisplayWall(id = null, wName = "벽2", start = Vec2(5f, 0f), end = Vec2(5f, 4f), status = WallStatus.IN_USE),
        DisplayWall(id = null, wName = "벽3", start = Vec2(5f, 4f), end = Vec2(0f, 4f), status = WallStatus.POSSIBLE),
        DisplayWall(id = null, wName = "벽4", start = Vec2(0f, 4f), end = Vec2(0f, 0f), status = WallStatus.NOT_POSSIBLE)
    )
    FloorPlanPreview(initialWalls = walls, readOnly = false)
}

@Preview(showBackground = true, name = "읽기 전용")
@Composable
private fun PreviewFloorPlan_ReadOnly() {
    val walls = listOf(
        DisplayWall(id = null, wName = "벽1", start = Vec2(0f, 0f), end = Vec2(6f, 0f), status = WallStatus.POSSIBLE),
        DisplayWall(id = null, wName = "벽2", start = Vec2(6f, 0f), end = Vec2(6f, 3f), status = WallStatus.IN_USE),
        DisplayWall(id = null, wName = "벽3", start = Vec2(6f, 3f), end = Vec2(3f, 3f), status = WallStatus.RESERVED),
        DisplayWall(id = null, wName = "벽4", start = Vec2(3f, 3f), end = Vec2(3f, 5f), status = WallStatus.POSSIBLE),
        DisplayWall(id = null, wName = "벽5", start = Vec2(3f, 5f), end = Vec2(0f, 5f), status = WallStatus.NOT_POSSIBLE),
        DisplayWall(id = null, wName = "벽6", start = Vec2(0f, 5f), end = Vec2(0f, 0f), status = WallStatus.POSSIBLE)
    )
    FloorPlanPreview(initialWalls = walls, readOnly = true)
}
