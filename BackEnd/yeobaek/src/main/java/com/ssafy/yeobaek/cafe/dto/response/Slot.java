package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "전시 슬롯 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Slot {
    @Schema(description = "슬롯 ID", example = "1")
    private Long slotId;

    @Schema(description = "슬롯 이름", example = "1번 벽")
    private String slotName;

    @Schema(description = "벽면 폭 (cm)", example = "200")
    private Integer widthCm;

    @Schema(description = "벽면 높이 (cm)", example = "250")
    private Integer heightCm;

    @Schema(description = "설치 방식", example = "RAIL")
    private String installationMethod;

    @Schema(description = "기본 수수료율", example = "15.0")
    private Double commissionRate;

    @Schema(description = "전시 위치 설명", example = "카페 입구 오른쪽 벽면")
    private String locationDescription;

    @Schema(description = "AR 공간 스캔 데이터", example = "{}")
    private String arScanData;
}
