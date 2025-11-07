package com.ssafy.yeobaek.common.enums;

public enum ExhibitionStatus {
    AVAILABLE,  // 사용 가능
    EXHIBITING, // 전시 중
    SCHEDULED;   // 예약됨

    /**
     * ExhibitionStatus → WallStatus 변환 메서드
     */
    public WallStatus toWallStatus() {
        return switch (this) {
            case AVAILABLE -> WallStatus.AVAILABLE;
            case EXHIBITING -> WallStatus.EXHIBITING;
            case SCHEDULED -> WallStatus.SCHEDULED;
        };
    }
}
