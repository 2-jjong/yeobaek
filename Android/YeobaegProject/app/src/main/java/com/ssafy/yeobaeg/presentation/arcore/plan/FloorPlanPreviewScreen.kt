package com.ssafy.yeobaeg.presentation.arcore.plan

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.ssafy.yeobaeg.domain.model.DisplaySlot
import com.ssafy.yeobaeg.domain.model.SlotStatus
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sqrt

/* ====== 상수 ====== */
private const val HIT_THRESHOLD_PX = 30f
private const val WALL_STROKE_PX   = 20f
private const val LABEL_MARGIN_PX  = 8f
private const val OUTLINE_STROKE_PX = 6f
private const val LABEL_PAD_H = 10f   // 라벨 수평 패딩
private const val LABEL_PAD_V = 8f    // 라벨 수직 패딩

@SuppressLint("DefaultLocale")
@Composable
fun FloorPlanPreview(
    initialWalls: List<DisplaySlot>,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onWallTapped: (Long) -> Unit = {},
    activeWallId: Long? = null,
) {
    val wallsState = remember(initialWalls) { initialWalls.toMutableStateList() }
    val walls: List<DisplaySlot> = wallsState

    if (walls.isEmpty()) {
        Box(modifier = modifier) {
            Text("도면 데이터가 없습니다.", modifier = Modifier.align(Alignment.Center))
        }
        return
    }

    // 벽 세그먼트(화면 좌표 + wallId)
    val mappedSegmentsState = remember {
        mutableStateOf(emptyList<Triple<Offset, Offset, Long>>())
    }

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val padOuter = 16.dp
        val padOuterPx = with(density) { padOuter.toPx() }

        // ---- 월드 바운딩 ----
        val allPoints = walls.flatMap { listOf(it.start, it.end) }
        val minX = allPoints.minOf { it.x }
        val maxX = allPoints.maxOf { it.x }
        val minY = allPoints.minOf { it.y }
        val maxY = allPoints.maxOf { it.y }
        val contentW = (maxX - minX).coerceAtLeast(1e-3f)
        val contentH = (maxY - minY).coerceAtLeast(1e-3f)

        // ---- 부모 픽셀 ----
        val parentWpx = with(density) { maxWidth.toPx() }
        val parentHpx = with(density) { maxHeight.toPx() }

        // ---- 스케일 ----
        val s = min(
            (parentWpx - 2 * padOuterPx) / contentW,
            (parentHpx - 2 * padOuterPx) / contentH
        ).coerceAtLeast(1f)

        val canvasWpx = (contentW * s + 2 * padOuterPx).coerceAtMost(parentWpx)
        val canvasHpx = (contentH * s + 2 * padOuterPx).coerceAtMost(parentHpx)
        val canvasW = with(density) { canvasWpx.toDp() }
        val canvasH = with(density) { canvasHpx.toDp() }

        // 라벨용 페인트(두 줄)
        val namePaint = remember {
            Paint().apply {
                isAntiAlias = true
                color = "#0F172A".toColorInt() // slate-900
                textSize = 36f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
        }
        val sizePaint = remember {
            Paint().apply {
                isAntiAlias = true
                color = "#334155".toColorInt() // slate-700
                textSize = 30f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
            }
        }

        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(canvasW, canvasH)
                .align(Alignment.Center)
        ) {
            val canvasModifier = Modifier
                .matchParentSize()
                .pointerInput(walls, readOnly) {
                    detectTapGestures(
                        onTap = { tap ->
                            val (wallId, dist) = findNearestSegmentById(mappedSegmentsState.value, tap)
                            if (wallId != null && dist <= HIT_THRESHOLD_PX) {
                                if (!readOnly) {
                                    val idx = walls.indexOfFirst { it.wid == wallId }
                                    if (idx >= 0) toggleWallPossible(wallsState, idx)
                                }
                                onWallTapped(wallId) // 선택/하이라이트/디테일 시트 갱신은 외부에서
                            }
                        },
                        onLongPress = { tap ->
                            if (readOnly) return@detectTapGestures
                            val (wallId, dist) = findNearestSegmentById(mappedSegmentsState.value, tap)
                            if (wallId != null && dist <= HIT_THRESHOLD_PX) {
                                val idx = walls.indexOfFirst { it.wid == wallId }
                                if (idx >= 0) toggleWallInUse(wallsState, idx)
                            }
                        }
                    )
                }

            Canvas(modifier = canvasModifier) {
                fun map(x: Float, y: Float): Offset {
                    val mx = (x - minX) * s + padOuterPx
                    val my = (y - minY) * s + padOuterPx
                    return Offset(mx, my)
                }

                // 외곽선
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

                // 세그먼트 수집(히트 테스트용)
                val mappedSegments = ArrayList<Triple<Offset, Offset, Long>>(walls.size)

                fun drawGlowLine(a: Offset, b: Offset, neon: Color) {
                    val w1 = WALL_STROKE_PX * 2.2f
                    val w2 = WALL_STROKE_PX * 1.7f
                    val w3 = WALL_STROKE_PX * 1.3f
                    drawLine(neon.copy(alpha = 0.25f), a, b, strokeWidth = w1)
                    drawLine(neon.copy(alpha = 0.40f), a, b, strokeWidth = w2)
                    drawLine(neon.copy(alpha = 0.65f), a, b, strokeWidth = w3)
                }

                // 이미 배치된 라벨 박스들(겹침 회피용)
                val placedLabelRects = mutableListOf<RectF>()

                fun drawWall(wall: DisplaySlot, emphasize: Boolean) {
                    val a = map(wall.start.x, wall.start.y)
                    val b = map(wall.end.x, wall.end.y)

                    if (emphasize) drawGlowLine(a, b, Color(0xFF22D3EE))

                    val (stroke, color) = wallStyle(wall.status, emphasize)
                    drawLine(color = color, start = a, end = b, strokeWidth = stroke)
                    mappedSegments.add(Triple(a, b, wall.wid))

                    // 라벨(두 줄: 이름, 크기)
                    val dx = wall.end.x - wall.start.x
                    val dy = wall.end.y - wall.start.y
                    val len = sqrt(dx * dx + dy * dy).coerceAtLeast(1e-6f)

                    val midX = (wall.start.x + wall.end.x) * 0.5f
                    val midY = (wall.start.y + wall.end.y) * 0.5f
                    val mid = map(midX, midY)

                    // 법선(외측 기준)
                    val ccw = isCCW(mappedVertices)
                    var nx =  dy / len
                    var ny = -dx / len
                    if (!ccw) { nx = -nx; ny = -ny }

                    // 텍스트 구성
                    val name = wall.wName.ifBlank { "벽 ${wall.wid}" }
                    val widthText = wall.widthText
                    val sizeText = if (wall.height <= 0f) {
                        widthText
                    } else {
                        val hText = wall.heightText
                        "$widthText × $hText"
                    }

                    // 두 줄 폭/높이 계산
                    val nameW = namePaint.measureText(name)
                    val sizeW = sizePaint.measureText(sizeText)
                    val textW = maxOf(nameW, sizeW)

                    val fmName = namePaint.fontMetrics
                    val fmSize = sizePaint.fontMetrics
                    val lineH1 = (fmName.descent - fmName.ascent)
                    val lineH2 = (fmSize.descent - fmSize.ascent)
                    val totalH = lineH1 + lineH2 + LABEL_PAD_V * 2f
                    val totalW = textW + LABEL_PAD_H * 2f

                    // 배치 후보(충돌 회피): 법선 ±, 접선 ±, 배수 증가
                    val tx = -ny // 접선 x
                    val ty = nx   // 접선 y
                    val baseOut = WALL_STROKE_PX * 0.5f + LABEL_MARGIN_PX
                    val candidates = buildList {
                        val scales = floatArrayOf(1f, 1.6f, 2.2f, 2.8f, 3.4f)
                        val dirs = arrayOf(
                            Pair(nx, ny),
                            Pair(-nx, -ny),
                            Pair(tx, ty),
                            Pair(-tx, -ty),
                        )
                        for (sc in scales) {
                            for ((ux, uy) in dirs) {
                                add(Offset(
                                    mid.x + ux * baseOut * sc,
                                    mid.y + uy * baseOut * sc
                                ))
                            }
                        }
                    }

                    // 후보 순회하며 겹침/화면 밖 회피
                    var chosenCenter: Offset? = null
                    var chosenRect: RectF? = null
                    for (c in candidates) {
                        val left = c.x - totalW / 2f
                        val top  = c.y - totalH / 2f
                        val right = c.x + totalW / 2f
                        val bottom = c.y + totalH / 2f
                        val r = RectF(left, top, right, bottom)

                        val outOfCanvas = left < 0f || top < 0f || right > size.width || bottom > size.height
                        val overlaps = placedLabelRects.any { it.intersects(r.left, r.top, r.right, r.bottom) }
                        if (!overlaps && !outOfCanvas) {
                            chosenCenter = c
                            chosenRect = r
                            break
                        }
                    }
                    // 실패 시 기본 위치라도 사용
                    if (chosenCenter == null) {
                        chosenCenter = Offset(
                            mid.x + nx * baseOut,
                            mid.y + ny * baseOut
                        )
                        chosenRect = RectF(
                            chosenCenter.x - totalW / 2f,
                            chosenCenter.y - totalH / 2f,
                            chosenCenter.x + totalW / 2f,
                            chosenCenter.y + totalH / 2f
                        )
                    }
                    placedLabelRects += (chosenRect ?: return)

                    // 두 줄 그리기(가운데 정렬)
                    val cx = chosenCenter.x
                    val top = chosenRect.top + LABEL_PAD_V

                    // baseline 계산: top에서 ascent 보정
                    val baselineName = top - namePaint.fontMetrics.ascent
                    val baselineSize = baselineName + lineH2 // 바로 아래 줄

                    if (wall.status != SlotStatus.UNAVAILABLE) {
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(name, cx, baselineName, namePaint)
                            drawText(sizeText, cx, baselineSize, sizePaint)
                        }
                    }
                }

                // 비활성 → 활성 순으로 그려 하이라이트가 위로 오게
                walls.filter { it.wid != activeWallId }.forEach { drawWall(it, emphasize = false) }
                walls.firstOrNull { it.wid == activeWallId }?.let { drawWall(it, emphasize = true) }

                mappedSegmentsState.value = mappedSegments

                // 코너 렌더
                mappedVertices.dropLast(1).forEach { p ->
                    drawCircle(Color(0xFF1B5E20), radius = 15f, center = p)
                }
            }
        }
    }
}

/* ====== 히트 테스트 ====== */
private fun findNearestSegmentById(
    segments: List<Triple<Offset, Offset, Long>>,
    tap: Offset
): Pair<Long?, Float> {
    var bestWallId: Long? = null
    var bestDist = Float.POSITIVE_INFINITY
    segments.forEach { (a, b, wallId) ->
        val d = pointToSegmentDistance(tap, a, b)
        if (d < bestDist) {
            bestDist = d
            bestWallId = wallId
        }
    }
    return bestWallId to bestDist
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

/* ====== 스타일/유틸 ====== */
private fun wallStyle(status: SlotStatus, emphasize: Boolean): Pair<Float, Color> {
    val baseColor = when (status) {
        SlotStatus.UNAVAILABLE -> Color(0xFFCBD5E1)
        SlotStatus.AVAILABLE     -> Color(0xFF10B981)
        SlotStatus.EXHIBITING       -> Color(0xFFEF4444)
        SlotStatus.RESERVED     -> Color(0xFFF59E0B)
    }
    val stroke = if (emphasize) WALL_STROKE_PX * 1.15f else WALL_STROKE_PX
    val color  = if (emphasize) baseColor.copy(alpha = 0.95f) else baseColor
    return stroke to color
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

/* ====== 내부 토글 로직(편집 모드에서만 사용) ====== */
private fun toggleWallPossible(walls: MutableList<DisplaySlot>, index: Int) {
    if (index !in walls.indices) return
    val w = walls[index]
    val newStatus = when (w.status) {
        SlotStatus.UNAVAILABLE -> SlotStatus.AVAILABLE
        SlotStatus.AVAILABLE, SlotStatus.EXHIBITING, SlotStatus.RESERVED -> SlotStatus.UNAVAILABLE
    }
    walls[index] = w.copy(status = newStatus)
}

private fun toggleWallInUse(walls: MutableList<DisplaySlot>, index: Int) {
    if (index !in walls.indices) return
    val w = walls[index]
    val newStatus = when (w.status) {
        SlotStatus.EXHIBITING -> SlotStatus.AVAILABLE
        else -> SlotStatus.EXHIBITING
    }
    walls[index] = w.copy(status = newStatus)
}
