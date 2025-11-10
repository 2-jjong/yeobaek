package com.ssafy.yeobaeg.presentation.arcore.vertical

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.ssafy.yeobaeg.presentation.arcore.common.detachAnchorSafe
import com.ssafy.yeobaeg.presentation.arcore.common.distance
import com.ssafy.yeobaeg.presentation.arcore.common.posePosition
import com.ssafy.yeobaeg.presentation.arcore.common.removeNodeSafe
import com.ssafy.yeobaeg.presentation.arcore.common.updateCylinderBetween3D
import com.ssafy.yeobaeg.presentation.arcore.horizontal.CenterReticle
import com.ssafy.yeobaeg.presentation.arcore.horizontal.DistanceHUD
import com.ssafy.yeobaeg.presentation.arcore.measure.Seg
import com.ssafy.yeobaeg.presentation.arcore.measure.VerticalMeasureViewModel
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraStream
import io.github.sceneview.math.Position
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

private const val BASE_LINE_HEIGHT = 1.0f

@Composable
fun VerticalMeasureCamera(
    modifier: Modifier = Modifier,
    onMeasured: (Float) -> Unit = {},
    onCancel: () -> Unit = {},
    viewModel: VerticalMeasureViewModel = hiltViewModel()
) {
    val engine = rememberEngine()
    val materialLoader = rememberMaterialLoader(engine)
    val cameraStream = rememberARCameraStream(materialLoader)
    val nodes = rememberNodes()

    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    var frame by remember { mutableStateOf<Frame?>(null) }
    var lastPoint by remember { mutableStateOf<Position?>(null) }
    var liveDistance by remember { mutableStateOf<Float?>(null) }
    val segments = remember { mutableStateListOf<Seg>() }
    var centerHasPlane by remember { mutableStateOf(false) }
    var liveLine by remember { mutableStateOf<CylinderNode?>(null) }
    var liveDot by remember { mutableStateOf<SphereNode?>(null) }

    var ready by remember { mutableStateOf(false) }
    var isDisposing by remember { mutableStateOf(false) }
    var sceneKey by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val maxPoints = 2
    val canAddPoint = segments.size < maxPoints

    LaunchedEffect(viewSize) {
        ready = if (viewSize.width > 0 && viewSize.height > 0) { awaitFrame(); true } else false
    }

    fun clearInScene() {
        liveLine?.let { removeNodeSafe(it, nodes) }
        liveLine = null
        liveDot?.let { removeNodeSafe(it, nodes) }
        liveDot = null
        segments.forEach { seg ->
            seg.line?.let { removeNodeSafe(it, nodes) }
            removeNodeSafe(seg.marker, nodes)
            detachAnchorSafe(seg.anchorNode)
            removeNodeSafe(seg.anchorNode, nodes)
        }
        segments.clear()
        lastPoint = null
        liveDistance = null
    }
    fun disposeWithSceneAlive() {
        if (isDisposing) return
        isDisposing = true
        ready = false
        clearInScene()
    }
    fun disposeOnComposeDisposeOnly() {
        liveLine = null; liveDot = null
        segments.clear(); lastPoint = null; liveDistance = null
        ready = false; isDisposing = true
    }

    DisposableEffect(Unit) { onDispose { disposeOnComposeDisposeOnly() } }
    BackHandler(enabled = true) {
        disposeWithSceneAlive()
        viewModel.onBackPressed()
        onCancel()
    }

    MaterialTheme {
        Box(
            modifier.onSizeChanged { viewSize = it }
        ) {
            if (ready) {
                key(sceneKey) {
                    ARScene(
                        modifier = Modifier.fillMaxSize(),
                        engine = engine,
                        materialLoader = materialLoader,
                        cameraStream = cameraStream,
                        childNodes = nodes,
                        sessionConfiguration = { s: Session, cfg: Config ->
                            cfg.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                            cfg.focusMode  = Config.FocusMode.AUTO
                            cfg.depthMode =
                                if (s.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
                                    Config.DepthMode.AUTOMATIC
                                else Config.DepthMode.DISABLED
                            cfg.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                            cfg.lightEstimationMode  = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                            cfg.planeFindingMode     = Config.PlaneFindingMode.VERTICAL
                        },
                        onSessionFailed = {
                            scope.launch {
                                ready = false
                                awaitFrame()
                                if (viewSize.width > 0 && viewSize.height > 0) {
                                    ready = true; sceneKey++
                                }
                            }
                        },
                        onSessionUpdated = { _, updated ->
                            if (!ready || isDisposing) return@ARScene
                            frame = updated
                            val cam = updated.camera
                            if (cam.trackingState != TrackingState.TRACKING) {
                                centerHasPlane = false
                                liveLine?.isVisible = false
                                liveDot?.isVisible  = false
                                return@ARScene
                            }
                            val cx = viewSize.width * 0.5f
                            val cy = viewSize.height * 0.5f
                            if (cx <= 0f || cy <= 0f) return@ARScene

                            val planeHit = runCatching {
                                updated.hitTest(cx, cy).firstOrNull { hr ->
                                    val pl = hr.trackable as? Plane ?: return@firstOrNull false
                                    pl.type == Plane.Type.VERTICAL && pl.isPoseInPolygon(hr.hitPose)
                                }
                            }.getOrNull()
                            centerHasPlane = planeHit != null

                            val centerWorld = planeHit?.posePosition()

                            if (centerWorld != null) {
                                if (liveDot == null) {
                                    liveDot = SphereNode(engine = engine, radius = 0.006f).apply { isShadowCaster = false }
                                    nodes += liveDot!!
                                }
                                liveDot!!.position = centerWorld
                                liveDot!!.isVisible = true
                            } else liveDot?.isVisible = false

                            liveDistance = if (centerWorld != null && lastPoint != null) {
                                distance(lastPoint!!, centerWorld)
                            } else null

                            val a = lastPoint
                            val b = centerWorld
                            if (a != null && b != null) {
                                if (liveLine == null) {
                                    liveLine = CylinderNode(engine = engine, radius = 0.006f, height = BASE_LINE_HEIGHT)
                                        .apply { isShadowCaster = false }
                                    nodes += liveLine!!
                                }
                                updateCylinderBetween3D(liveLine!!, a, b)
                                liveLine!!.isVisible = true
                            } else liveLine?.isVisible = false
                        },
                        onTouchEvent = { _: MotionEvent, _: io.github.sceneview.collision.HitResult? -> false },
                        planeRenderer = true
                    )
                }
            }

            if (ready && centerHasPlane) {
                CenterReticle(Modifier.align(Alignment.Center), isVertical = true)
                Box(Modifier.align(Alignment.TopCenter).padding(top = 12.dp)) {
                    Surface(color = Color(0x332196F3), shape = MaterialTheme.shapes.small) {
                        Text("벽 감지됨", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF1565C0), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            // 포인트 추가 (⭐ 2개가 되면 자동 측정/종료)
            Button(
                onClick = {
                    val f = frame ?: return@Button
                    if (f.camera.trackingState != TrackingState.TRACKING) return@Button
                    if (!canAddPoint) return@Button

                    val cx = viewSize.width * 0.5f
                    val cy = viewSize.height * 0.5f
                    if (cx <= 0f || cy <= 0f) return@Button

                    val planeHit = runCatching {
                        f.hitTest(cx, cy).firstOrNull { hr ->
                            val pl = hr.trackable as? Plane ?: return@firstOrNull false
                            pl.type == Plane.Type.VERTICAL && pl.isPoseInPolygon(hr.hitPose)
                        }
                    }.getOrNull() ?: return@Button

                    val world = planeHit.posePosition()
                    viewModel.addCorner(world, planeHit.trackable as? Plane)

                    val anchor = planeHit.createAnchor() ?: return@Button
                    val anchorNode = AnchorNode(engine, anchor = anchor)
                    val marker = SphereNode(engine = engine, radius = 0.01f).apply { isShadowCaster = false }
                    anchorNode.addChildNode(marker)
                    nodes += anchorNode

                    lastPoint?.let { prev ->
                        val len = distance(prev, world)
                        val line = CylinderNode(engine = engine, radius = 0.003f, height = BASE_LINE_HEIGHT)
                            .apply { isShadowCaster = false }
                        updateCylinderBetween3D(line, prev, world)
                        nodes += line
                        segments += Seg(anchorNode, marker, line, len, world)
                    } ?: run {
                        segments += Seg(anchorNode, marker, null, 0f, world)
                    }
                    lastPoint = world

                    // ⭐ 여기서 자동 완료 처리
                    if (segments.size == 2) {
                        val h = distance(segments[0].world, segments[1].world)
                        scope.launch {
                            onMeasured(h*100)           // 1) 결과 전달
                            disposeWithSceneAlive() // 2) Scene 정리
                            awaitFrame()            // 3) 프레임 반영
                            onCancel()              // 4) 부모에서 카메라 닫기 처리(showCamera=false 등)
                        }
                    }
                },
                enabled = ready && centerHasPlane && canAddPoint,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
            ) {
                Text(
                    when {
                        !centerHasPlane   -> "벽을 인식 중…"
                        !canAddPoint      -> "포인트 최대 개수 도달"
                        segments.isEmpty() -> "벽 하단 포인트 추가"
                        else              -> "벽 상단 포인트 추가"
                    }
                )
            }

            // ⛔ 저장 버튼 제거(자동 종료이므로 불필요)
            // HUD(실시간/마지막만 표시)
            DistanceHUD(
                isVertical = true,
                liveMeters = liveDistance,
                lastSegmentMeters = segments.lastOrNull()?.lengthM?.takeIf { it > 0f },
                onUndo = {
                    if (segments.isNotEmpty()) {
                        val last = segments.removeAt(segments.lastIndex)
                        last.line?.let { removeNodeSafe(it, nodes) }
                        removeNodeSafe(last.marker, nodes)
                        detachAnchorSafe(last.anchorNode)
                        removeNodeSafe(last.anchorNode, nodes)
                        lastPoint = segments.lastOrNull()?.world
                        viewModel.undoCorner()
                    }
                },
                onClear = { clearInScene() }
            )
        }
    }
}

