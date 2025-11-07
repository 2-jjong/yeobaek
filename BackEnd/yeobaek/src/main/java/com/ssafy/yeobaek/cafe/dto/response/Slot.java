package com.ssafy.yeobaek.cafe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.enums.WallStatus;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Slot {

    @Schema(description = "벽 ID", example = "1")
    private Long wallId;

    @Schema(description = "벽 이름", example = "1번 벽")
    private String wallName;

    @Schema(description = "벽면 폭 (cm)", example = "200")
    private Double width;

    @Schema(description = "벽면 높이 (cm)", example = "250")
    private Double height;

    @Schema(description = "전시 상태", example = "AVAILABLE")
    private WallStatus status;

    @Schema(description = "수수료율", example = "15.0")
    private Double commissionRate;

    @Schema(description = "기타 요구 사항", example = "")
    private String requirements;

    @Schema(description = "현재 전시 정보 (전시 중/예정인 경우만)")
    private ExhibitionInfo currentExhibition;
}
