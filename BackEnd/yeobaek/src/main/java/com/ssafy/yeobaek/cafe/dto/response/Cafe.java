package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "카페 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cafe {
    @Schema(description = "카페 ID", example = "1")
    private Long cafeId;

    @Schema(description = "매장명", example = "카페 여백")
    private String cafeName;

    @Schema(description = "카페 주소", example = "서울시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "연락처", example = "02-1234-5678")
    private String contact;

    @Schema(description = "인테리어 스타일 목록", example = "[\"모던\", \"미니멀\"]")
    private List<String> categories;

    @Schema(description = "카페 이미지 목록")
    private List<CafeImage> images;
}
