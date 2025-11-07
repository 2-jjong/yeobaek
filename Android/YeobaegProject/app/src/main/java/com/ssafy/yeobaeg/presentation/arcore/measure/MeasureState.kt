package com.ssafy.yeobaeg.presentation.arcore.measure

import com.google.ar.core.Pose
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.node.CylinderNode
import io.github.sceneview.node.SphereNode


/** AR 측정 공통 상태 */
data class MeasureState(
    val planePose: Pose? = null,
    val cornersWorld: List<Position> = emptyList(),
    val isBuilding: Boolean = false
)

data class Seg(
    val anchorNode: AnchorNode,
    val marker: SphereNode,
    val line: CylinderNode?,
    val lengthM: Float,
    val world: Position
)