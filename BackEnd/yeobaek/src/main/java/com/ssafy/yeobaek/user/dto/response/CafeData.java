package com.ssafy.yeobaek.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeData {
    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "카페명", example = "아트 카페")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "카페 소개", example = "작가들의 작품을 전시하는 아트 카페입니다.")
    private String description;

    @Schema(description = "연락처", example = "02-1234-5678")
    private String contact;

    @Schema(description = "영업시간", example = "매일 09:00 - 22:00")
    private String businessHours;

    @Schema(description = "평가 통계")
    private RatingStatsData ratingStats;
}
