// FloorPlanPreviewScreen.kt (전체)
package com.ssafy.yeobaeg.presentation.arcore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.yeobaeg.domain.model.WallStatus
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun FloorPlanPreview(
    viewModel: ARMeasureViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val walls = state.value.walls

    Box(Modifier.fillMaxSize()) {
        if (walls.isEmpty()) {
            Text("도면 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        // 탭 히트테스트를 위한 '화면 좌표상의 선분' 보관
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
                                val segs = mappedSegmentsState.value
                                if (segs.isEmpty()) return@detectTapGestures
                                val (idx, dist) = findNearestSegment(segs, tap)
                                if (idx != null && dist <= 30f) {
                                    viewModel.toggleWallPossible(idx)
                                }
                            },
                            onLongPress = { tap ->
                                val segs = mappedSegmentsState.value
                                if (segs.isEmpty()) return@detectTapGestures
                                val (idx, dist) = findNearestSegment(segs, tap)
                                if (idx != null && dist <= 30f) {
                                    viewModel.toggleWallInUse(idx)
                                }
                            }
                        )
                    }
            ) {
                // ---- 0) 그릴 데이터 준비 ----
                // 모든 점(시작/끝)을 모아 bounds 계산
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
                    // 상단이 +Y (미러 방지)
                    return Offset(mx, my)
                }

                // ---- 1) 외곽 가이드 Path (연한 회색) ----
                // 벽들이 폐곡선을 이룬다는 가정하에, 꼭짓점 순서를 구성한다.
                // 일반적으로 walls[i].end == walls[i+1].start 구조일 것이라 가정.
                val orderedVertices = walls.map { it.start } +
                        listOf(walls.last().end) // 마지막 꼭짓점으로 닫기
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
                    val guideStroke = 6f
                    drawPath(
                        path = outline,
                        color = Color(0xFFE5E7EB),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = guideStroke)
                    )
                }

                // ---- 2) 벽 선분 그리기 + 세그먼트(화면좌표) 수집 ----
                val mappedSegments = ArrayList<Pair<Offset, Offset>>(walls.size)
                for ((i, wall) in walls.withIndex()) {
                    val a = map(wall.start.x, wall.start.y)
                    val b = map(wall.end.x, wall.end.y)
                    val (stroke, color) = when (wall.status) {
                        WallStatus.NOT_POSSIBLE -> 20f to Color(0xFFCBD5E1)   // 회색
                        WallStatus.POSSIBLE     -> 20f to Color(0xFF10B981)  // 초록
                        WallStatus.IN_USE       -> 20f to Color(0xFFEF4444)  // 빨강
                        WallStatus.RESERVED -> 20f to Color(0xFFF59E0B)  // 주황
                    }
                    drawLine(color = color, start = a, end = b, strokeWidth = stroke)
                    mappedSegments += a to b
                }
                mappedSegmentsState.value = mappedSegments

                // ---- 3) 라벨(길이) 그리기: 불가능(NOT_POSSIBLE)은 생략 ----
                // 폴리곤 방향 (라벨을 바깥으로 밀기 위한 법선 방향 판단)
                fun signedArea(vertices: List<Offset>): Float {
                    var area = 0f
                    for (i in 0 until vertices.size - 1) {
                        val p = vertices[i]
                        val q = vertices[i + 1]
                        area += p.x * q.y - q.x * p.y
                    }
                    return area * 0.5f
                }
                val isCCW = signedArea(mappedVertices) > 0f

                val native = drawContext.canvas.nativeCanvas
                val textPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.parseColor("#334155")
                    textSize = max(24f, 36f)
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val wallStrokePx = 20f
                val marginPx = 8f
                val outwardOffsetPx = wallStrokePx * 0.5f + marginPx

                for ((i, wall) in walls.withIndex()) {
                    if (wall.status == WallStatus.NOT_POSSIBLE) continue

                    val dx = wall.end.x - wall.start.x
                    val dy = wall.end.y - wall.start.y
                    val lengthM = sqrt(dx * dx + dy * dy)
                    if (!lengthM.isFinite() || lengthM <= 1e-6f) continue

                    val midX = (wall.start.x + wall.end.x) * 0.5f
                    val midY = (wall.start.y + wall.end.y) * 0.5f
                    val mid = map(midX, midY)

                    // 단위 외부 법선(폴리곤 CCW 기준)
                    val len = sqrt(dx * dx + dy * dy)
                    var nx =  dy / len      // (dx,dy)에 수직인 후보
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

                // ---- 4) 코너 포인트(옵션) ----
                mappedVertices.dropLast(1).forEach { p -> // 마지막(닫는 점) 제외
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
