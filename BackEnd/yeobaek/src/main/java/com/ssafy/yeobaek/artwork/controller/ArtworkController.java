package com.ssafy.yeobaek.artwork.controller;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.request.UpdateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.*;
import com.ssafy.yeobaek.artwork.service.ArtworkBookmarkService;
import com.ssafy.yeobaek.artwork.service.ArtworkService;
import com.ssafy.yeobaek.common.dto.Pagination;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.enums.ArtworkSortBy;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@Tag(name = "작품 관리", description = "작품 관리 API")
@RestController
@RequestMapping("/api/v1/artworks")
@RequiredArgsConstructor
@Validated
public class ArtworkController {

    private final ArtworkService artworkService;
    private final int MOCK_ARTWORK_COUNT = 20;
    private final ArtworkBookmarkService artworkBookmarkService;

    @Operation(summary = "작품 등록", description = "작가가 피드 형식으로 작품을 등록합니다. 이미지는 최소 1장, 최대 10장까지 등록 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "정상 처리",
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
                                            name = "이미지 개수 오류",
                                            value = "{\"code\": \"AW4001\", \"message\": \"작품 이미지는 최소 1장 최대 10장까지 등록 가능합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품명 길이 오류",
                                            value = "{\"code\": \"AW4003\", \"message\": \"작품명은 1~100자 이내여야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 설명 길이 오류",
                                            value = "{\"code\": \"AW4004\", \"message\": \"작품 설명은 최대 500자까지 입력 가능합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 카테고리",
                                            value = "{\"code\": \"AW4006\", \"message\": \"유효하지 않은 카테고리입니다\"}"
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
                            examples = @ExampleObject(
                                    value = "{\"code\": \"AW4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                            )
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
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ResponseDto<Void>> createArtwork(@Valid @RequestBody CreateArtworkRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        artworkService.createArtwork(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }

    @Operation(summary = "작품 수정", description = "등록된 작품 정보를 수정합니다. 전시 중인 작품은 규격(가로/세로) 변경이 불가능합니다. 수정하고자 하는 필드만 포함하여 요청하면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
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
                                            name = "이미지 개수 오류",
                                            description = "작품 이미지가 1~10장 범위를 벗어난 경우",
                                            value = "{\"code\": \"AW4001\", \"message\": \"작품 이미지는 최소 1장 최대 10장까지 등록 가능합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품명 길이 오류",
                                            description = "작품명이 1~100자 범위를 벗어난 경우",
                                            value = "{\"code\": \"AW4003\", \"message\": \"작품명은 1~100자 이내여야 합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 설명 길이 오류",
                                            description = "작품 설명이 500자를 초과한 경우",
                                            value = "{\"code\": \"AW4004\", \"message\": \"작품 설명은 최대 500자까지 입력 가능합니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 카테고리",
                                            description = "존재하지 않는 카테고리 값이 입력된 경우",
                                            value = "{\"code\": \"AW4006\", \"message\": \"유효하지 않은 카테고리입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 입력값",
                                            description = "가격, 규격, 연도 등의 값이 유효 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
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
                                            name = "작품 수정 권한 없음",
                                            description = "본인의 작품이 아닌 경우",
                                            value = "{\"code\": \"AW4032\", \"message\": \"본인의 작품만 수정/삭제할 수 있습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "권한 없음",
                                            description = "작가 권한이 없는 경우",
                                            value = "{\"code\": \"AW4031\", \"message\": \"해당 요청에 대한 권한이 없습니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "작품을 찾을 수 없음",
                                    description = "해당 ID의 작품이 존재하지 않는 경우",
                                    value = "{\"code\": \"AW4041\", \"message\": \"존재하지 않는 작품입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "전시 중인 작품 규격 변경 불가",
                                    description = "전시 중인 작품의 가로/세로 크기를 변경하려는 경우",
                                    value = "{\"code\": \"AW4091\", \"message\": \"전시 중인 작품은 규격을 변경할 수 없습니다\"}"
                            )
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
    @PatchMapping("/{artworkId}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ResponseDto<Void>> updateArtwork(
            @Parameter(description = "수정할 작품 ID", required = true)
            @PathVariable Long artworkId,
            @Valid @RequestBody UpdateArtworkRequest request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        artworkService.updateArtwork(userId, artworkId, request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "작품 삭제", description = "작품을 삭제합니다. 전시 중이거나 전시 예정인 작품은 삭제할 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": null, \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"AW4032\", \"data\": null, \"message\": \"본인의 작품만 수정/삭제할 수 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"AW4041\", \"data\": null, \"message\": \"존재하지 않는 작품입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "충돌",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"AW4092\", \"data\": null, \"message\": \"전시 중이거나 전시 예정인 작품은 삭제할 수 없습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @DeleteMapping("/{artworkId}")
    public ResponseEntity<ResponseDto<Void>> deleteArtwork(
            @Parameter(description = "삭제할 작품 ID", required = true)
            @PathVariable Long artworkId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "내 작품 목록 조회", description = "내가 등록한 작품 목록을 조회합니다. 홈에서 작품 피드를 표시할 때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "작품이 있는 경우",
                                            description = "사용자가 등록한 작품이 있는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artworks\": [{\"artworkId\": 1, \"title\": \"여름의 여백\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork1.jpg\", \"price\": 500000, \"category\": \"PAINTING\", \"widthCm\": 100, \"heightCm\": 150, \"exhibitionStatus\": \"AVAILABLE\", \"bookmarkCount\": 42}, {\"artworkId\": 2, \"title\": \"가을의 정적\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork2.jpg\", \"price\": 750000, \"category\": \"SCULPTURE\", \"widthCm\": 80, \"heightCm\": 120, \"exhibitionStatus\": \"EXHIBITING\", \"bookmarkCount\": 35}], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 15, \"totalPages\": 1, \"hasNext\": false}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품이 없는 경우",
                                            description = "사용자가 등록한 작품이 없는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artworks\": [], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 0, \"totalPages\": 0, \"hasNext\": false}}, \"message\": \"정상 처리\"}"
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 회원",
                                    value = "{\"code\": \"AU4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                            )
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
    @GetMapping("/me")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<ResponseDto<ArtworkListResponse>> getMyArtworks(
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        ArtworkListResponse data = artworkService.getUserArtworks(userId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "특정 작가의 작품 목록 조회", description = "특정 작가의 작품 목록을 조회합니다. 작가 프로필 페이지에서 작품 목록을 표시할 때 사용합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "작품이 있는 경우",
                                            description = "작가가 등록한 작품이 있는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artworks\": [{\"artworkId\": 101, \"title\": \"도시의 밤\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork101.jpg\", \"price\": 600000, \"category\": \"PHOTOGRAPHY\", \"widthCm\": 120, \"heightCm\": 80, \"exhibitionStatus\": \"AVAILABLE\", \"bookmarkCount\": 28}, {\"artworkId\": 102, \"title\": \"산속의 오두막\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork102.jpg\", \"price\": 450000, \"category\": \"PAINTING\", \"widthCm\": 90, \"heightCm\": 70, \"exhibitionStatus\": \"AVAILABLE\", \"bookmarkCount\": 15}], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 23, \"totalPages\": 2, \"hasNext\": true}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품이 없는 경우",
                                            description = "작가가 등록한 작품이 없는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artworks\": [], \"pagination\": {\"currentPage\": 0, \"pageSize\": 20, \"totalElements\": 0, \"totalPages\": 0, \"hasNext\": false}}, \"message\": \"정상 처리\"}"
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 회원",
                                    value = "{\"code\": \"AU4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                            )
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
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseDto<ArtworkListResponse>> getUserArtworks(
            @Parameter(description = "작가 회원 ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        ArtworkListResponse data = artworkService.getUserArtworks(userId, page, size);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(
            summary = "작가/작품 목록 조회 및 검색",
            description = "작품 목록을 조회하거나 검색합니다. 키워드 검색, 필터링, 정렬 기능을 제공합니다.\n\n" +
                    "**주요 기능:**\n" +
                    "- 키워드가 있으면: 작가명과 작품명을 동시에 검색하여 결과 반환 (artists 필드 포함)\n" +
                    "- 키워드가 없으면: 전체 작품 목록 조회 (artists 필드는 빈 배열)\n" +
                    "- 카테고리 필터: 여러 카테고리 동시 선택 가능 (예: PORTRAIT, LANDSCAPE)\n" +
                    "- 규격 필터: 작품의 가로/세로 크기로 필터링 (카페 사장만 사용 가능)\n" +
                    "- 정렬: 최신순(LATEST) 또는 인기순(POPULARITY)\n" +
                    "- 페이징: 기본 20개씩, 최대 50개까지\n\n" +
                    "**권한:**\n" +
                    "- 로그인 필수\n" +
                    "- 규격 필터(가로/세로) 사용 시: 카페 사장만 가능"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "키워드 검색 (작가+작품)",
                                            description = "키워드로 검색 시 작가 목록과 작품 목록 모두 반환",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artists\": [{\"userId\": 1, \"artistName\": \"홍길동\", \"profileImageUrl\": \"https://s3.presigned.url/artist1.jpg\", \"introduction\": \"현대 미술을 전공한 신진 작가입니다.\", \"artworkCount\": 15}, {\"userId\": 2, \"artistName\": \"김작가\", \"profileImageUrl\": \"https://s3.presigned.url/artist2.jpg\", \"introduction\": \"추상화 전문 작가입니다.\", \"artworkCount\": 8}], \"artworks\": [{\"artworkId\": 101, \"title\": \"여름의 여백\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork101.jpg\"}, {\"artworkId\": 102, \"title\": \"봄날의 꿈\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork102.jpg\"}, {\"artworkId\": 103, \"title\": \"가을의 정적\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork103.jpg\"}], \"pagination\": {\"page\": 0, \"size\": 20, \"totalElements\": 3, \"totalPages\": 1, \"hasNext\": false}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "전체 작품 목록 (키워드 없음)",
                                            description = "키워드 없이 전체 작품 조회 시 작가 목록은 null",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artists\": [], \"artworks\": [{\"artworkId\": 1, \"title\": \"도시의 밤\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork1.jpg\"}, {\"artworkId\": 2, \"title\": \"산속의 오두막\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork2.jpg\"}, {\"artworkId\": 3, \"title\": \"추상의 세계\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork3.jpg\"}, {\"artworkId\": 4, \"title\": \"바다의 속삭임\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork4.jpg\"}, {\"artworkId\": 5, \"title\": \"황혼의 노래\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork5.jpg\"}], \"pagination\": {\"page\": 0, \"size\": 20, \"totalElements\": 150, \"totalPages\": 8, \"hasNext\": true}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "카테고리 필터 적용",
                                            description = "특정 카테고리만 조회 (예: PAINTING)",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artists\": [], \"artworks\": [{\"artworkId\": 10, \"title\": \"꽃병과 과일\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork10.jpg\"}, {\"artworkId\": 11, \"title\": \"푸른 하늘\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork11.jpg\"}], \"pagination\": {\"page\": 0, \"size\": 20, \"totalElements\": 45, \"totalPages\": 3, \"hasNext\": true}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "인기순 정렬",
                                            description = "인기순(조회수 + 북마크 수 * 9)으로 정렬된 결과",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artists\": [], \"artworks\": [{\"artworkId\": 50, \"title\": \"밤하늘의 별\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork50.jpg\"}, {\"artworkId\": 23, \"title\": \"새벽의 기도\", \"thumbnailUrl\": \"https://s3.presigned.url/artwork23.jpg\"}], \"pagination\": {\"page\": 0, \"size\": 20, \"totalElements\": 150, \"totalPages\": 8, \"hasNext\": true}}, \"message\": \"정상 처리\"}"
                                    ),
                                    @ExampleObject(
                                            name = "검색 결과 없음",
                                            description = "검색 조건에 맞는 작품이 없는 경우",
                                            value = "{\"code\": \"S0000\", \"data\": {\"artists\": [], \"artworks\": [], \"pagination\": {\"page\": 0, \"size\": 20, \"totalElements\": 0, \"totalPages\": 0, \"hasNext\": false}}, \"message\": \"정상 처리\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "키워드 길이 초과",
                                            description = "keyword가 100자를 초과한 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "카테고리 개수 초과",
                                            description = "categories가 8개를 초과한 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "가로 크기 범위 오류",
                                            description = "minWidth 또는 maxWidth가 1-500 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "세로 크기 범위 오류",
                                            description = "minHeight 또는 maxHeight가 1-500 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호 오류",
                                            description = "page가 0 미만인 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "페이지 크기 오류",
                                            description = "size가 1-50 범위를 벗어난 경우",
                                            value = "{\"code\": \"S4002\", \"message\": \"입력값이 유효하지 않습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 카테고리",
                                            description = "존재하지 않는 카테고리 값이 입력된 경우",
                                            value = "{\"code\": \"AW4006\", \"message\": \"유효하지 않은 카테고리입니다\"}"
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
                            examples = @ExampleObject(
                                    name = "규격 필터 권한 없음",
                                    description = "카페 사장이 아닌 사용자가 규격 필터(가로/세로) 사용 시",
                                    value = "{\"code\": \"AW4007\", \"message\": \"작품 규격 필터는 카페 사장만 사용할 수 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "존재하지 않는 사용자",
                                    description = "규격 필터 권한 확인 중 사용자 정보를 찾을 수 없는 경우",
                                    value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ResponseDto<ArtworkListResponse>> searchArtworks(
            @Parameter(description = "작품명, 작가명 검색 키워드 (최대 100자)")
            @Size(max = 100, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "카테고리 필터 (최대 8개까지 다중 선택)")
            @Size(max = 8, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) List<ArtCategory> categories,

            @Parameter(description = "작품 최소 가로 (1-500cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer minWidth,

            @Parameter(description = "작품 최대 가로 (1-500cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer maxWidth,

            @Parameter(description = "작품 최소 세로 (1-500cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer minHeight,

            @Parameter(description = "작품 최대 세로 (1-500cm)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 500, message = "INVALID_INPUT_VALUE")
            @RequestParam(required = false) Integer maxHeight,

            @Parameter(description = "정렬 기준 (LATEST: 최신 등록순, POPULARITY: 인기순)")
            @RequestParam(defaultValue = "LATEST") ArtworkSortBy sortBy,

            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @Min(value = 0, message = "INVALID_INPUT_VALUE")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "페이지 크기 (1-50, 기본값: 20)")
            @Min(value = 1, message = "INVALID_INPUT_VALUE")
            @Max(value = 50, message = "INVALID_INPUT_VALUE")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        ArtworkListResponse data = artworkService.searchArtworks(
                userId, keyword, categories,
                minWidth, maxWidth, minHeight, maxHeight,
                sortBy, page, size
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "추천 작품 목록 조회", description = "특정 카페에 어울리는 작품 목록을 추천합니다. 알고리즘 기반으로 카페 특성과 작품 특성을 매칭합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"CF4041\", \"data\": null, \"message\": \"존재하지 않는 카페입니다\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}")
                    )
            )
    })
    @GetMapping("/recommendations")
    public ResponseEntity<ResponseDto<ArtworkListResponse>> getRecommendedArtworks(
            @Parameter(description = "카페 ID", required = true)
            @RequestParam Long cafeId,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 20개로 증가
        List<ArtworkSummaryDto> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 300L); // 다른 ID로 시작

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListResponse data = new ArtworkListResponse(null, artworks, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "작품 상세 조회", description = "특정 작품의 상세 정보를 조회합니다. 작품 정보, 작가 정보, 전시 상태, 전시 장소 정보를 포함합니다. 조회수가 증가합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"AW4041\", \"message\": \"존재하지 않는 작품입니다\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}")
                    )
            )
    })
    @GetMapping("/{artworkId}")
    public ResponseEntity<ResponseDto<ArtworkDetailResponse>> getArtworkDetail(
            @Parameter(description = "조회할 작품 ID", required = true)
            @PathVariable Long artworkId
    ) {
        // 현재 로그인한 사용자 ID 가져오기
        Long userId = SecurityUtil.getCurrentUserId();
        ArtworkDetailResponse data = artworkService.getArtworkDetail(artworkId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "인기 작품 목록 조회", description = "조회수, 북마크 수, 평가를 기반으로 인기 작품을 추천합니다. 최대 20개까지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}")
                    )
            )
    })
    @GetMapping("/popular")
    public ResponseEntity<ResponseDto<PopularArtworksResponse>> getPopularArtworks(
            @Parameter(description = "조회할 작품 수 (기본값: 10, 최대: 20)")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        // Mock 데이터 - size에 따라 최대 20개까지 생성
        int count = Math.min(size, MOCK_ARTWORK_COUNT);
        List<ArtworkSummaryDto> artworks = createMockArtworks(count, 400L);

        PopularArtworksResponse data = new PopularArtworksResponse(artworks);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "작품 북마크 토글", description = "작품을 북마크에 추가하거나 제거합니다.\n\n" + " 북마크가 없으면 추가하고, 이미 있으면 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"isBookmarked\" : true}, \"message\": \"정상 처리\"}"
                            )
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "회원 정보 없음",
                                            value = "{\"code\": \"US4041\", \"message\": \"존재하지 않는 회원입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "작품 정보 없음",
                                            value = "{\"code\": \"AW4041\", \"message\": \"존재하지 않는 작품입니다\"}"
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
    @PostMapping("/{artworkId}/bookmarks")
    public ResponseEntity<ResponseDto<BookmarkToggleResponse>> toggleBookmark(
            @Parameter(description = "북마크 토글할 작품 ID", required = true)
            @PathVariable Long artworkId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        BookmarkToggleResponse data = artworkBookmarkService.toggleBookmark(userId, artworkId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "작품 북마크 목록 조회", description = "현재 사용자가 북마크한 작품 목록을 조회합니다. 최신 북마크 순으로 정렬됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "유효하지 않은 토큰", value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"),
                                    @ExampleObject(name = "만료된 토큰", value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}")
                    )
            )
    })
    @GetMapping("/bookmarks")
    public ResponseEntity<ResponseDto<ArtworkListResponse>> getBookmarkedArtworks(
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 20개로 증가
        List<ArtworkSummaryDto> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 500L);

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListResponse data = new ArtworkListResponse(null, artworks, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    private List<ArtworkSummaryDto> createMockArtworks(int count, Long startId) {
        String[] titles = {"여름의 여백", "가을의 정적", "봄날의 꿈", "겨울의 침묵", "도시의 밤", "산속의 오두막", "추상의 세계", "바다의 속삭임", "황혼의 노래", "새벽의 기도", "꽃병과 과일", "푸른 하늘", "고요한 아침", "빛과 그림자", "시간의 흐름", "미지의 공간", "노을 진 풍경", "밤하늘의 별", "꿈속의 정원", "자유로운 영혼"};

        return IntStream.range(0, count)
                .mapToObj(i -> {
                    long id = startId + i;
                    String title = titles[i % titles.length] + " #" + id;
                    String thumbnailUrl = "https://example.com/artwork" + (i + 1) + ".jpg";

                    return ArtworkSummaryDto.builder()
                            .artworkId(id)
                            .title(title)
                            .thumbnailUrl(thumbnailUrl)
                            .build();
                })
                .toList();
    }
}