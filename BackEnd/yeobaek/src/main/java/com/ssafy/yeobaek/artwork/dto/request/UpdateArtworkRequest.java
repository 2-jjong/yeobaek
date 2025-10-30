package com.ssafy.yeobaek.artwork.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "작품 수정 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArtworkRequest {
    @Schema(description = "작품명 (1~100자)", example = "가을의 여백")
    private String title;

    @Schema(description = "작품 설명 (최대 1000자)", example = "가을날 낙엽이 지는 풍경을 표현한 작품입니다.")
    private String description;

    @Schema(description = "작품 가격 (원)", example = "550000")
    private Integer price;

    @Schema(description = "카테고리 (풍경화, 인물화, 정물화, 장르화, 역사화, 기타)", example = "풍경화")
    private String category;

    @Schema(description = "작품 가로 (cm)", example = "80")
    private Integer widthCm;

    @Schema(description = "작품 세로 (cm)", example = "60")
    private Integer heightCm;

    @Schema(description = "작품 무게 (kg)", example = "2.5")
    private Double weightKg;

    @Schema(description = "제작 연도 (YYYY)", example = "2024")
    private Integer productionYear;

    @Schema(description = "작품 이미지 목록 (최소 1장, 최대 10장)")
    private List<ImageRequest> images;
}
