package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 이미지 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CafeImage {
    @Schema(description = "이미지 ID", example = "1")
    private Long imageId;

    @Schema(description = "이미지 URL", example = "https://example.com/cafe1.jpg")
    private String imageUrl;

    @Schema(description = "대표 이미지 여부", example = "true")
    private Boolean isPrimary;

    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder;
}
