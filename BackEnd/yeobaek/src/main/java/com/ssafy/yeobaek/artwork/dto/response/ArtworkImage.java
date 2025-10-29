package com.ssafy.yeobaek.artwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "작품 이미지 정보")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkImage {
    @Schema(description = "이미지 ID", example = "1")
    private Long imageId;

    @Schema(description = "이미지 URL", example = "https://example.com/image1.jpg")
    private String imageUrl;

    @Schema(description = "표시 순서", example = "1")
    private Integer displayOrder;
}
