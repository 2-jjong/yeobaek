package com.ssafy.yeobaek.exhibition.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;
import com.ssafy.yeobaek.exhibition.service.ExhibitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "전시 관리", description = "전시 관리 API")
@RestController
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @Operation(
            summary = "전시 제안 생성",
            description = "카페 사장 또는 작가가 전시를 제안합니다. 제안자는 자동으로 동의 상태가 되며, 상대방이 동의하면 전시가 확정됩니다.\n\n" +
                    "**카페 사장이 제안**: 특정 작품을 자신의 카페에 전시하고 싶을 때\n" +
                    "**작가가 제안**: 자신의 작품을 특정 카페에 전시하고 싶을 때"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "전시 제안 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "필수 값 누락",
                                            value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "수수료율 범위 오류",
                                            value = "{\"code\": \"EX4001\", \"message\": \"수수료율은 0~15 사이여야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "전시 기간 오류",
                                            value = "{\"code\": \"EX4003\", \"message\": \"전시 종료일은 시작일보다 늦어야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "과거 날짜",
                                            value = "{\"code\": \"EX4004\", \"message\": \"전시 시작일은 오늘 이후여야 합니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "카페 권한 없음",
                                            description = "본인의 카페가 아닌 경우",
                                            value = "{\"code\": \"CF4032\", \"message\": \"본인의 카페만 수정/삭제할 수 있습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 권한 없음",
                                            description = "본인의 작품이 아닌 경우",
                                            value = "{\"code\": \"AW4032\", \"message\": \"본인의 작품만 수정/삭제할 수 있습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "권한 없음",
                                            description = "카페 사장 또는 작가가 아닌 경우",
                                            value = "{\"code\": \"S4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "카페를 찾을 수 없음",
                                            value = "{\"code\": \"CF4041\", \"message\": \"존재하지 않는 카페입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품을 찾을 수 없음",
                                            value = "{\"code\": \"AW4041\", \"message\": \"존재하지 않는 작품입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "슬롯을 찾을 수 없음",
                                            value = "{\"code\": \"CF4042\", \"message\": \"존재하지 않는 슬롯입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "슬롯이 카페에 속하지 않음",
                                            value = "{\"code\": \"CF4044\", \"message\": \"해당 카페에 존재하지 않는 슬롯입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 예약",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "슬롯 중복 예약",
                                            value = "{\"code\": \"EX4091\", \"message\": \"해당 기간에 이미 예약된 슬롯입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 중복 예약",
                                            value = "{\"code\": \"EX4092\", \"message\": \"해당 기간에 이미 다른 전시가 예정된 작품입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('CAFE_OWNER', 'ARTIST')")
    public ResponseEntity<ResponseDto<Void>> createExhibitionProposal(
            @Valid @RequestBody CreateExhibitionRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        exhibitionService.createExhibitionProposal(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }
}
