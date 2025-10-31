package com.ssafy.yeobaeg.domain.model

enum class SlotStatus { AVAILABLE, ONGOING, RESERVED }

// 전시 상태
enum class WallStatus {
    NOT_POSSIBLE,   // 전시 불가능 (회색)
    POSSIBLE,       // 전시 가능   (초록)
    IN_USE,          // 전시 중     (빨강)
    RESERVED        // 전시 예약   (노랑)
}

data class Vec2(val x: Float, val y: Float) {
    operator fun plus(o: Vec2) = Vec2(x + o.x, y + o.y)
    operator fun minus(o: Vec2) = Vec2(x - o.x, y - o.y)
    operator fun times(s: Float) = Vec2(x * s, y * s)
}

// 도면 조회 응답 dto
data class DisplayWall(
    val id: Long? = null,
    val wName: String,         // "벽 A", "벽 B" ...
    val start: Vec2,          // 로컬 XZ 시작점
    val end: Vec2,            // 로컬 XZ 끝점
    val status: WallStatus = WallStatus.POSSIBLE
)

// 도면 생성 요청 dto
data class CreateDisplayWallRequest(
    val wName: String,         // "벽 A", "벽 B" ...
    val start: Vec2,          // 로컬 XZ 시작점
    val end: Vec2,            // 로컬 XZ 끝점
    val status: Boolean      // 전시 가능 여부 true 이면 POSSIBLE, false 이면 NOT_POSSIBLE
)

// 도면 생성 응답 dto
data class CreateDisplayWallResponse(
    val wid: Long,
    val wName: String,         // "벽 A", "벽 B" ...
    val start: Vec2,          // 로컬 XZ 시작점
    val end: Vec2,            // 로컬 XZ 끝점
    val status: WallStatus      // 전시 가능 여부 ("POSSIBLE", "NOT_POSSIBLE", "IN_USE", "RESERVED")
)

// 전시 슬롯 조회 응답 dto
data class DisplaySlot(
    val wid: Long = 0L,
    val wName: String = "",
    val width: Float = 0f,
    val height: Float = 0f,
    val status: SlotStatus = SlotStatus.AVAILABLE,
    val commission: Float = 0f, // 수수료
    val requirements: String = "", // 기타 요구 사항
    val deadline: String = "", // 전시 마감 일자
    val artId: Long = 0L,
    val artTitle: String = "",
    val artist: String = "",
    val artImgUrl: String = ""
) {
    val sizeText = "${width.toInt()}m x ${height.toInt()}m"
}

// 전시 슬롯 생성 요청 dto
data class CreateDisplaySlotRequest(
    val wid: Long,
    val width: Float,
    val height: Float,
    val commission: Float, // 수수료
    val requirements: String, // 기타 요구 사항
)
