package com.ssafy.yeobaek.cafe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "전시 슬롯 수정 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSlotRequest {

    @Schema(description = "수수료율", example = "10.0")
    private Double commission;

    @Schema(description = "기타 요구 사항", example = "홍보용 작가 소개 카드 또는 작품 설명 자료를 함께 제공해주세요.")
    private String requirements;
}
