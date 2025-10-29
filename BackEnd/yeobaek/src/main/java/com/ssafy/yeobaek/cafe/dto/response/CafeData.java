package com.ssafy.yeobaek.cafe.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 응답 데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CafeData {
    @Schema(description = "카페 정보")
    private Cafe cafe;
}
