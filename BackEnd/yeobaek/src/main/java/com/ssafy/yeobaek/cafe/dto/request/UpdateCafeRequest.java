package com.ssafy.yeobaek.cafe.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "카페 정보 수정 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCafeRequest {
    @Schema(description = "매장명 (1~20자)", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "카페 소개 (최대 500자)", example = "조용하고 아늑한 분위기의 카페입니다.")
    private String description;

    @Schema(description = "연락처", example = "02-1234-5678")
    private String contact;

    @Schema(description = "영업 시작 시간 (HH:mm 형식)", example = "09:00")
    private String businessHoursStart;

    @Schema(description = "영업 종료 시간 (HH:mm 형식)", example = "22:00")
    private String businessHoursEnd;

    @Schema(description = "일일 평균 방문객 수", example = "100~200명")
    private String dailyVisitors;

    @Schema(description = "인테리어 스타일 (다중 선택)", example = "[\"모던\", \"미니멀\"]")
    private List<String> categories;

    @Schema(description = "카페 이미지 목록 (최소 3장, 최대 10장)")
    private List<String> images;
}
