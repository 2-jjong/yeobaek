package com.ssafy.yeobaek.artwork.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "작품 상세 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtworkDetail {
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "여름의 여백")
    private String title;

    @Schema(description = "작품 설명", example = "여름날 햇살이 비치는 풍경을 표현한 작품입니다.")
    private String description;

    @Schema(description = "작품 가격 (원)", example = "500000")
    private Integer price;

    @Schema(description = "카테고리", example = "풍경화")
    private String category;

    @Schema(description = "작품 가로 (cm)", example = "80")
    private Integer widthCm;

    @Schema(description = "작품 세로 (cm)", example = "60")
    private Integer heightCm;

    @Schema(description = "작품 무게 (kg)", example = "2.5")
    private Double weightKg;

    @Schema(description = "제작 연도", example = "2024")
    private Integer productionYear;

    @Schema(description = "작품 상태 (AVAILABLE: 전시 가능, EXHIBITING: 전시 중)", example = "AVAILABLE")
    private String status;

    @Schema(description = "조회수", example = "150")
    private Integer viewCount;

    @Schema(description = "북마크 수", example = "42")
    private Integer bookmarkCount;

    @Schema(description = "현재 사용자의 북마크 여부", example = "false")
    private Boolean isBookmarked;

    @Schema(description = "작품 이미지 목록")
    private List<ArtworkImage> images;

    @Schema(description = "작가 정보")
    private Artist artist;

    @Schema(description = "현재 전시 정보 (전시 중일 때만)")
    private Exhibition currentExhibition;
}
