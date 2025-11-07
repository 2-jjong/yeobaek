package com.ssafy.yeobaek.artwork.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.enums.ExhibitionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작품 상세 요약 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtworkSummaryDetail {
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "여름의 여백")
    private String title;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "작가명", example = "홍길동")
    private String artistName;

    @Schema(description = "작가 프로필 이미지 URL", example = "https://example.com/artist-profile.jpg")
    private String artistProfileImageUrl;

    @Schema(description = "작품 가격 (원)", example = "500000")
    private Integer price;

    @Schema(description = "카테고리", example = "풍경화")
    private ArtCategory category;

    @Schema(description = "작품 가로 (cm)", example = "80")
    private Integer widthCm;

    @Schema(description = "작품 세로 (cm)", example = "60")
    private Integer heightCm;

    @Schema(description = "전시 상태", example = "AVAILABLE")
    private ExhibitionStatus status;

    @Schema(description = "북마크 수", example = "42")
    private Integer bookmarkCount;

    @Schema(description = "전시 장소(카페명)", example = "카페 여백")
    private String cafeName;
}
