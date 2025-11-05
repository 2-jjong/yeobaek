package com.ssafy.yeobaek.cafe.dto.request;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import com.ssafy.yeobaek.common.validation.ValidCategoryIds;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "카페 공간 등록 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDisplayWallRequest {

	@Schema(description = "벽 이름", example = "벽 A")
	@NotBlank(message = "MISSING_REQUIRED_FIELD")
	@Size(min = 2, max = 15, message = "CAFE_INVALID_NAME_LENGTH")
	private String wallName;

	@Schema(description = "시작점")
	@NotEmpty(message = "MISSING_REQUIRED_FIELD")
	private Vec2 start;

	@Schema(description = "끝점")
	@NotEmpty(message = "MISSING_REQUIRED_FIELD")
	private Vec2 end;

	@Schema(description = "전시 가능 여부", example = "true")
	@NotEmpty(message = "MISSING_REQUIRED_FIELD")
	private Boolean status;
}
