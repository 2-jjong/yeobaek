package com.ssafy.yeobaek.cafe.dto.response;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import com.ssafy.yeobaek.common.enums.WallStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "카페 공간 응답")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wall {

	@Schema(description = "벽 id", example = "1")
	private Long wallId;

	@Schema(description = "벽 이름", example = "벽 A")
	private String wallName;

	@Schema(description = "시작점")
	private Vec2 start;

	@Schema(description = "끝점")
	private Vec2 end;

	@Schema(description = "전시 가능 여부", example = "true")
	private WallStatus status;
}
