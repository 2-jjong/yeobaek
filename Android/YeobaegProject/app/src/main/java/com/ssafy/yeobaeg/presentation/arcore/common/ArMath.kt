package com.ssafy.yeobaeg.presentation.arcore.common

import android.annotation.SuppressLint
import com.google.ar.core.HitResult
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.math.Position
import io.github.sceneview.node.CylinderNode
import kotlin.math.atan2
import kotlin.math.sqrt

private const val BASE_LINE_HEIGHT = 1.0f
private const val EPS = 1e-4f

/** HitResult -> 월드 좌표 */
fun HitResult.posePosition(): Position =
    Position(hitPose.tx(), hitPose.ty(), hitPose.tz())

/** 3D 거리 */
fun distance(a: Position, b: Position): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

/** 바닥(수평) 두 점을 잇는 실린더 배치 */
fun updateCylinderBetweenGround(line: CylinderNode, a: Position, b: Position) {
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

/** 임의의 3D 두 점을 잇는 실린더 배치 */
fun updateCylinderBetween3D(line: CylinderNode, a: Position, b: Position) {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val dz = b.z - a.z
    val len = sqrt(dx * dx + dy * dy + dz * dz)
    if (!len.isFinite() || len < EPS) { line.isVisible = false; return }
    val mid = Position((a.x + b.x) * 0.5f, (a.y + b.y) * 0.5f, (a.z + b.z) * 0.5f)
    line.position = mid
    val lenXZ = sqrt(dx * dx + dz * dz).coerceAtLeast(1e-6f)
    val yawRad = atan2(dx, dz)
    val pitchRad = -atan2(lenXZ, dy)
    val rad2deg = 180f / Math.PI.toFloat()
    line.rotation = Float3(pitchRad * rad2deg, yawRad * rad2deg, 0f)
    line.scale = Float3(1f, len / BASE_LINE_HEIGHT, 1f)
    line.isVisible = true
}

/** 수평면에서 yaw (Filament용 XYZ 오일러) */
fun eulerYawOnGround(dx: Float, dz: Float): Float3 {
    val lenXZ = sqrt(dx * dx + dz * dz).coerceAtLeast(1e-6f)
    val yawRad = atan2(dx / lenXZ, dz / lenXZ)
    val rad2deg = 180f / Math.PI.toFloat()
    return Float3(90f, yawRad * rad2deg, 0f)
}

@SuppressLint("DefaultLocale")
fun fmt(m: Float) = "${(m * 100).toInt()} cm"