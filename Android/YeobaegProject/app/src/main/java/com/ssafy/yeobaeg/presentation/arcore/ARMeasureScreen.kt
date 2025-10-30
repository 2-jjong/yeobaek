// ARMeasureScreen.kt
package com.ssafy.yeobaeg.presentation.arcore

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.ar.core.Config
import com.google.ar.core.Coordinates2d
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.math.Position
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import java.nio.ByteOrder
import kotlin.math.atan2
import kotlin.math.sqrt

private const val MIN_POINT_GAP_M = 0.10f

@Composable
fun ARMeasureScreen(
    viewModel: ARMeasureViewModel = hiltViewModel()
) {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val cameraStream = rememberARCameraStream(materialLoader)
    val nodes = rememberNodes()

    // 화면 사이즈(뷰 좌표용, px)
    var viewSize by remember { mutableStateOf(IntSize.Zero) }

    var frame by remember { mutableStateOf<Frame?>(null) }
    var lastPoint by remember { mutableStateOf<Position?>(null) }
    var liveDistance by remember { mutableStateOf<Float?>(null) }
    var totalDistance by remember { mutableFloatStateOf(0f) }
    val segments = remember { mutableStateListOf<Seg>() }
    var centerHasFloor by remember { mutableStateOf(false) }
    var liveLine by remember { mutableStateOf<CylinderNode?>(null) }

    // 네비 직전 ARScene 안전하게 내려주기
    var arMounted by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()



    // VM 상태(StateFlow)
    val state by viewModel.uiState.collectAsState()

    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .onSizeChanged { viewSize = it }
        ) {
            if (arMounted) {
                ARScene(
                    modifier = Modifier.fillMaxSize(),
                    engine = engine,
                    materialLoader = materialLoader,
                    cameraStream = cameraStream,
                    childNodes = nodes,
                    sessionConfiguration = { s: Session, cfg: Config ->
                        cfg.depthMode =
                            if (s.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                                Config.DepthMode.AUTOMATIC
                            else
                                Config.DepthMode.DISABLED
                        cfg.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                        cfg.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    },
                    onSessionUpdated = { _, updated ->
                        frame = updated

                        val cam = updated.camera
                        if (cam.trackingState != TrackingState.TRACKING) {
                            centerHasFloor = false
                            liveLine?.isVisible = false
                            return@ARScene
                        }

                        // 화면 중앙(뷰 좌표 px)
                        val cx = viewSize.width * 0.5f
                        val cy = viewSize.height * 0.5f
                        if (cx <= 0f || cy <= 0f) return@ARScene

                        // 중앙 히트 (뷰 좌표로 hitTest)
                        val planeHit = runCatching {
                            updated.hitTest(cx, cy).firstOrNull { hr ->
                                val pl = hr.trackable as? Plane ?: return@firstOrNull false
                                pl.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                        pl.isPoseInPolygon(hr.hitPose)
                            }
                        }.getOrNull()
                        centerHasFloor = planeHit != null

                        // 깊이 기반 Fallback
                        val centerWorld = planeHit?.posePosition()
                            ?: runCatching { updated.worldPositionFromDepth(cx, cy) }.getOrNull()

                        liveDistance = if (centerWorld != null && lastPoint != null) {
                            distance(lastPoint!!, centerWorld)
                        } else null

                        val a = lastPoint
                        val b = centerWorld
                        if (a != null && b != null) {
                            if (liveLine == null) {
                                liveLine = CylinderNode(
                                    engine = engine,
                                    radius = 0.006f,
                                    height = BASE_LINE_HEIGHT
                                ).apply { isShadowCaster = false }
                                nodes += liveLine!!
                            }
                            updateCylinderBetweenGround(liveLine!!, a, b)
                            liveLine!!.isVisible = true
                        } else {
                            liveLine?.isVisible = false
                        }
                    },
                    onTouchEvent = { _: MotionEvent, _: io.github.sceneview.collision.HitResult? -> false },
                    planeRenderer = true
                )
            }

            if (centerHasFloor && arMounted) {
                CenterReticle(Modifier.align(Alignment.Center))
            }

            // 포인트 추가 (뷰 좌표 중앙으로 hitTest)
            Button(
                onClick = {
                    val f = frame ?: return@Button
                    val cam = f.camera
                    if (cam.trackingState != TrackingState.TRACKING) return@Button
                    val cx = viewSize.width * 0.5f
                    val cy = viewSize.height * 0.5f
                    if (cx <= 0f || cy <= 0f) return@Button

                    val planeHit = runCatching {
                        f.hitTest(cx, cy).firstOrNull { hr ->
                            val pl = hr.trackable as? Plane ?: return@firstOrNull false
                            pl.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                    pl.isPoseInPolygon(hr.hitPose)
                        }
                    }.getOrNull() ?: return@Button

                    val world = Position(
                        planeHit.hitPose.tx(),
                        planeHit.hitPose.ty(),
                        planeHit.hitPose.tz()
                    )

                    // 🔸 추가: 최소 간격(10cm) 미만이면 포인트 생성/저장 안 함
                    if (lastPoint != null && distance(lastPoint!!, world) < MIN_POINT_GAP_M) {
                        return@Button
                    }

                    // VM에 저장
                    viewModel.addCorner(world, planeHit.trackable as? Plane)

                    // 화면 시각화(마커/선)
                    val anchor = planeHit.createAnchor() ?: return@Button
                    val anchorNode = io.github.sceneview.ar.node.AnchorNode(engine, anchor = anchor)
                    val marker = SphereNode(engine = engine, radius = 0.01f).apply { isShadowCaster = false }
                    anchorNode.addChildNode(marker)
                    nodes += anchorNode

                    lastPoint?.let { prev ->
                        val len = distance(prev, world)
                        val line = CylinderNode(
                            engine = engine,
                            radius = 0.003f,
                            height = BASE_LINE_HEIGHT
                        ).apply { isShadowCaster = false }
                        updateCylinderBetweenGround(line, prev, world)
                        nodes += line
                        totalDistance += len
                        segments += Seg(marker = marker, line = line, lengthM = len, world = world)
                    } ?: run {
                        segments += Seg(marker = marker, line = null, lengthM = 0f, world = world)
                    }
                    lastPoint = world
                },
                enabled = centerHasFloor && arMounted,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
            ) { Text(if (centerHasFloor) "중앙에 포인트 추가" else "바닥을 인식 중…") }

            // 도면 생성: 네비 직전 ARScene 언마운트로 크래시 방지
            Button(
                onClick = {
                    scope.launch {
                        // 현재 프레임 마무리
                        awaitFrame()

                        // 노드 정리 + ARScene 언마운트
                        disposeSceneNodes(nodes, segments, liveLine)
                        liveLine = null
                        arMounted = false

                        // 언마운트 반영
                        awaitFrame()

                        // VM 빌드 & 네비
                        viewModel.buildAndNavigate()

                        disposeSceneNodes(nodes, segments, liveLine)
                        liveLine = null
                        lastPoint = null
                        liveDistance = null
                        totalDistance = 0f
                        viewModel.clearAll()
                    }
                },
                enabled = (!state.isBuilding && state.cornersWorld.size >= 3 && arMounted),
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 16.dp, bottom = 24.dp)
            ) {
                Text(if (state.isBuilding) "계산 중…" else "도면 생성")
            }

            DistanceHUD(
                liveMeters = liveDistance,
                lastSegmentMeters = segments.lastOrNull()?.lengthM?.takeIf { it > 0f },
                totalMeters = totalDistance,
                onUndo = {
                    if (segments.isNotEmpty()) {
                        val last = segments.removeAt(segments.lastIndex)
                        last.line?.let { ln -> ln.parent?.removeChildNode(ln); nodes.remove(ln) }
                        last.marker.parent?.removeChildNode(last.marker); nodes.remove(last.marker)
                        if (segments.isEmpty()) { lastPoint = null; totalDistance = 0f }
                        else { totalDistance -= last.lengthM; lastPoint = segments.last().world }
                        viewModel.undoCorner()
                    }
                },
                onClear = {
                    disposeSceneNodes(nodes, segments, liveLine)
                    liveLine = null
                    lastPoint = null
                    liveDistance = null
                    totalDistance = 0f
                    viewModel.clearAll()
                }
            )
        }
    }
}


/* ==== 화면 로컬 유틸 ==== */

private const val BASE_LINE_HEIGHT = 1.0f
private const val EPS = 1e-4f

data class Seg(
    val marker: SphereNode,
    val line: CylinderNode?,
    val lengthM: Float,
    val world: Position
)

private fun eulerYawOnGround(dx: Float, dz: Float): Float3 {
    val lenXZ = sqrt(dx * dx + dz * dz).coerceAtLeast(1e-6f)
    val yawRad = atan2(dx / lenXZ, dz / lenXZ)
    val rad2deg = 180f / Math.PI.toFloat()
    return Float3(90f, yawRad * rad2deg, 0f)
}

private fun updateCylinderBetweenGround(line: CylinderNode, a: Position, b: Position) {
    val dx = b.x - a.x
    val dz = b.z - a.z
    val lenXZ = sqrt(dx * dx + dz * dz)
    if (!lenXZ.isFinite() || lenXZ < EPS) { line.isVisible = false; return }
    val floorY = (a.y + b.y) * 0.5f
    val mid = Position((a.x + b.x) * 0.5f, floorY, (a.z + b.z) * 0.5f)
    line.position = mid
    line.rotation = eulerYawOnGround(dx, dz)
    line.scale = Float3(1f, lenXZ / BASE_LINE_HEIGHT, 1f)
    line.isVisible = true
}

@Composable
private fun DistanceHUD(
    liveMeters: Float?,
    lastSegmentMeters: Float?,
    totalMeters: Float,
    onUndo: () -> Unit,
    onClear: () -> Unit
) {
    val pad = 10.dp
    Column(
        Modifier.fillMaxWidth().padding(pad),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onUndo) { Text("되돌리기") }
            TextButton(onClick = onClear) { Text("초기화") }
        }
        Spacer(Modifier.height(8.dp))
        fun fmt(m: Float) = if (m < 1f) "${(m * 100).toInt()} cm" else String.format("%.2f m", m)
        Surface(tonalElevation = 4.dp, shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(12.dp)) {
                Text("실시간: ${liveMeters?.let(::fmt) ?: "-"}", style = MaterialTheme.typography.titleMedium)
                lastSegmentMeters?.let { Text("마지막 구간: ${fmt(it)}") }
                Text("총합: ${fmt(totalMeters)}")
            }
        }
    }
}

@Composable
private fun CenterReticle(modifier: Modifier = Modifier) {
    Box(
        modifier.size(22.dp)
            .background(Color(0x334CAF50), CircleShape)
            .border(2.dp, Color(0xFF4CAF50), CircleShape)
    )
}

fun Frame.worldPositionFromDepth(viewX: Float, viewY: Float): Position? {
    val cam = camera

    // 1) VIEW → IMAGE_PIXELS 좌표 변환
    val outImagePx = FloatArray(2)
    try {
        transformCoordinates2d(
            Coordinates2d.VIEW, floatArrayOf(viewX, viewY),
            Coordinates2d.IMAGE_PIXELS, outImagePx
        )
    } catch (_: Exception) {
        return null
    }
    val u = outImagePx[0].toInt()
    val v = outImagePx[1].toInt()

    // 2) 깊이 이미지 획득 (DEPTH16, mm 단위)
    val depthImage = try { acquireDepthImage16Bits() } catch (_: Exception) { null } ?: return null
    depthImage.use { img ->
        if (img.format != android.graphics.ImageFormat.DEPTH16) return null
        if (u !in 0 until img.width || v !in 0 until img.height) return null

        val plane = img.planes[0]
        val rowStride = plane.rowStride
        val buf = plane.buffer.order(ByteOrder.LITTLE_ENDIAN)

        val index = v * rowStride + u * 2
        if (index + 1 >= buf.capacity()) return null

        // ushort little-endian -> mm
        val depthMm: Int =
            (buf.get(index).toInt() and 0xFF) or ((buf.get(index + 1).toInt() and 0xFF) shl 8)
        if (depthMm <= 0) return null
        val depthM = depthMm / 1000f

        // 3) 카메라 이미지 Intrinsics
        val intr = cam.imageIntrinsics
        val fx = intr.focalLength[0]
        val fy = intr.focalLength[1]
        val cx = intr.principalPoint[0]
        val cy = intr.principalPoint[1]

        // 이미지 픽셀(u,v)을 이용해 역투영 (카메라 좌표계)
        val xCam = (outImagePx[0] - cx) / fx * depthM
        val yCam = (outImagePx[1] - cy) / fy * depthM
        val zCam = depthM

        // 4) 카메라 Pose로 월드 좌표 변환
        val world = cam.pose.transformPoint(floatArrayOf(xCam, yCam, zCam))
        return Position(world[0], world[1], world[2])
    }
}

/** HitResult → 월드 Position (Pose tx/ty/tz) */
fun HitResult.posePosition(): Position =
    Position(hitPose.tx(), hitPose.ty(), hitPose.tz())

fun distance(a: Position, b: Position): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

private fun disposeSceneNodes(
    nodes: MutableList<io.github.sceneview.node.Node>,
    segments: MutableList<Seg>,
    liveLine: CylinderNode?
) {
    // 라이브 라인
    liveLine?.let { it.parent?.removeChildNode(it); nodes.remove(it) }

    // 세그먼트 시각화 제거
    segments.forEach { seg ->
        seg.line?.let { ln -> ln.parent?.removeChildNode(ln); nodes.remove(ln) }
        seg.marker.parent?.removeChildNode(seg.marker); nodes.remove(seg.marker)
    }
    segments.clear()
}