package com.ssafy.yeobaek.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작가 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistData {
    @Schema(description = "작가 소개", example = "현대 미술을 전공한 신진 작가입니다.")
    private String introduction;

    @Schema(description = "등록한 작품 수", example = "15")
    private Integer artworkCount;

    @Schema(description = "전시 중인 작품 수", example = "5")
    private Integer exhibitingCount;

    @Schema(description = "평가 통계")
    private RatingStatsData ratingStats;
}
