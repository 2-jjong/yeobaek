package com.ssafy.yeobaek.artwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "전시 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exhibition {
    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "카페명", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String cafeAddress;

    @Schema(description = "카페 이미지 URL", example = "https://example.com/cafe.jpg")
    private String cafeImageUrl;

    @Schema(description = "슬롯 이름", example = "메인 전시 공간")
    private String slotName;

    @Schema(description = "전시 시작일 (YYYY-MM-DD)", example = "2024-01-01")
    private String exhibitionStartDate;

    @Schema(description = "전시 종료일 (YYYY-MM-DD)", example = "2024-03-31")
    private String exhibitionEndDate;
}
