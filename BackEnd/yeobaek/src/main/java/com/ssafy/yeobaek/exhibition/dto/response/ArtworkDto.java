package com.ssafy.yeobaek.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "작품 간략 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtworkDto {
    @Schema(description = "작품 ID", example = "1")
    private Long artworkId;

    @Schema(description = "작품명", example = "여름의 여백")
    private String title;

    @Schema(description = "작품 너비 (cm)", example = "120")
    private Integer widthCm;

    @Schema(description = "작품 높이 (cm)", example = "80")
    private Integer heightCm;

    @Schema(description = "작품 무게 (kg)", example = "5.2")
    private Double weightKg;

    @Schema(description = "작가명", example = "김작가")
    private String artistName;
}
