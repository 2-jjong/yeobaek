package com.ssafy.yeobaek.artwork.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작품 요약 정보 (작가 정보 포함)")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtworkSummaryWithArtistDto {
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "여름의 여백")
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "작품 가격 (원)", example = "500000")
    private Integer price;

    @Schema(description = "카테고리", example = "PAINTING")
    private String category;

    @Schema(description = "전시 장소(카페명)", example = "카페 여백")
    private String cafeName;

    @Schema(description = "현재 전시 중인지 여부", example = "false")
    private Boolean isExhibiting;

    @Schema(description = "북마크 수", example = "42")
    private Integer bookmarkCount;

    @Schema(description = "작가 프로필")
    private ArtistDto artistDto;
}
