package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "내 카페 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyCafeData {

    @Schema(description = "내 카페 정보")
    private MyCafeSummary cafeSummary;

    @Schema(description = "전시 슬롯 정보")
    private List<DisplaySlot> displaySlots;
}
