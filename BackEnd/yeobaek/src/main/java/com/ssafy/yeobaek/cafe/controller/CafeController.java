package com.ssafy.yeobaek.cafe.controller;

import com.ssafy.yeobaek.artwork.dto.response.BookmarkToggleData;
import com.ssafy.yeobaek.artwork.dto.response.Pagination;
import com.ssafy.yeobaek.cafe.dto.request.*;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.enums.ExhibitionStatus;
import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@Tag(name = "카페 관리", description = "카페 관리 API")
@RestController
@RequestMapping("/api/v1/cafes")
public class CafeController {

    private final int MOCK_CAFE_COUNT = 20;

    @Operation(summary = "카페 정보 등록", description = "카페 사장이 매장 상세 정보, 전시 슬롯, 전시 조건을 등록합니다. 회원가입 후 처음 카페 정보를 등록할 때 사용합니다. 이미지는 최소 3장, 최대 10장까지 등록 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
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
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "최소 이미지 개수 미달", value = "{\"code\": \"CF4001\", \"data\": null, \"message\": \"이미지는 최소 3장 이상 등록해야 합니다\"}"),
                                    @ExampleObject(name = "최대 이미지 개수 초과", value = "{\"code\": \"CF4002\", \"data\": null, \"message\": \"이미지는 최대 10장까지 등록 가능합니다\"}"),
                                    @ExampleObject(name = "대표 이미지 오류", value = "{\"code\": \"CF4003\", \"data\": null, \"message\": \"대표 이미지는 1개만 선택해야 합니다\"}"),
                                    @ExampleObject(name = "매장명 길이 오류", value = "{\"code\": \"CF4004\", \"data\": null, \"message\": \"매장명은 1~15자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "카페 소개 길이 오류", value = "{\"code\": \"CF4005\", \"data\": null, \"message\": \"카페 소개는 최대 500자까지 입력 가능합니다\"}"),
                                    @ExampleObject(name = "영업시간 형식 오류", value = "{\"code\": \"CF4007\", \"data\": null, \"message\": \"영업시간 형식이 올바르지 않습니다 (HH:mm)\"}"),
                                    @ExampleObject(name = "필수 값 누락", value = "{\"code\": \"S4001\", \"data\": null, \"message\": \"필수 입력값이 누락되었습니다\"}")
                            }
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
                                    value = "{\"code\": \"CF4031\", \"data\": null, \"message\": \"카페 사장만 등록할 수 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복 등록",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4091\", \"data\": null, \"message\": \"이미 등록된 카페가 있습니다\"}"
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
    @PostMapping
    public ResponseEntity<ResponseDto<Void>> createCafe(@RequestBody CreateCafeRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }

    @Operation(summary = "카페 정보 수정", description = "등록된 카페 정보를 수정합니다. 모든 필드는 선택적으로 수정 가능합니다 (전송된 필드만 수정됨). 슬롯 정보는 별도 API로 관리합니다.")
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
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "최소 이미지 개수 미달", value = "{\"code\": \"CF4001\", \"data\": null, \"message\": \"이미지는 최소 3장 이상 등록해야 합니다\"}"),
                                    @ExampleObject(name = "최대 이미지 개수 초과", value = "{\"code\": \"CF4002\", \"data\": null, \"message\": \"이미지는 최대 10장까지 등록 가능합니다\"}"),
                                    @ExampleObject(name = "대표 이미지 오류", value = "{\"code\": \"CF4003\", \"data\": null, \"message\": \"대표 이미지는 1개만 선택해야 합니다\"}"),
                                    @ExampleObject(name = "매장명 길이 오류", value = "{\"code\": \"CF4004\", \"data\": null, \"message\": \"매장명은 1~20자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "카페 소개 길이 오류", value = "{\"code\": \"CF4005\", \"data\": null, \"message\": \"카페 소개는 최대 500자까지 입력 가능합니다\"}"),
                                    @ExampleObject(name = "영업시간 형식 오류", value = "{\"code\": \"CF4007\", \"data\": null, \"message\": \"영업시간 형식이 올바르지 않습니다 (HH:mm)\"}")
                            }
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
                                    value = "{\"code\": \"CF4032\", \"data\": null, \"message\": \"본인의 카페만 수정/삭제할 수 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"data\": null, \"message\": \"존재하지 않는 카페입니다\"}"
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
    @PutMapping("/{cafeId}")
    public ResponseEntity<ResponseDto<Void>> updateCafe(
            @Parameter(description = "수정할 카페 ID", required = true)
            @PathVariable Long cafeId,
            @RequestBody UpdateCafeRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }

    @Operation(summary = "내 카페 정보 조회", description = "현재 로그인한 카페 사장의 카페 정보를 조회합니다. 수정 목적으로 사용되며, 전체 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
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
                                    value = "{\"code\": \"CF4031\", \"data\": null, \"message\": \"카페 사장만 조회할 수 있습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"data\": null, \"message\": \"존재하지 않는 카페입니다\"}"
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
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<CafeData>> getMyCafe() {
        // Mock 데이터
        List<CafeImage> images = List.of(
                new CafeImage(1L, "https://example.com/cafe1.jpg", true, 1),
                new CafeImage(2L, "https://example.com/cafe2.jpg", false, 2),
                new CafeImage(3L, "https://example.com/cafe3.jpg", false, 3)
        );

        Cafe cafe = new Cafe(
                1L, "카페 여백", "서울시 강남구 테헤란로 123",
                "02-1234-5678", List.of("모던", "미니멀"), images
        );

        CafeData data = new CafeData(cafe);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 북마크 목록 조회", description = "현재 사용자가 북마크한 카페 목록을 조회합니다. 최신 북마크 순으로 정렬됩니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
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
    @GetMapping("/bookmarks")
    public ResponseEntity<ResponseDto<CafeListData>> getBookmarkedCafes(
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터
        List<CafeSummary> cafes = createMockCafes(MOCK_CAFE_COUNT, 1L, null);

        Pagination pagination = new Pagination(page, size, (long) MOCK_CAFE_COUNT, (int) Math.ceil((double) MOCK_CAFE_COUNT / size), false);
        CafeListData data = new CafeListData(cafes, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "인기 카페 목록 조회", description = "조회수, 북마크 수, 평가를 기반으로 인기 카페를 추천합니다. 최대 20개까지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
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
    @GetMapping("/popular")
    public ResponseEntity<ResponseDto<PopularCafesData>> getPopularCafes(
            @Parameter(description = "조회할 카페 수 (기본값: 10, 최대: 20)")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        // Mock 데이터
        int count = Math.min(size, MOCK_CAFE_COUNT);
        List<CafeSummary> cafes = createMockCafes(count, 100L, null);

        PopularCafesData data = new PopularCafesData(cafes);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 목록 조회", description = "카페 목록을 조회하거나 검색합니다. 키워드 검색, 필터링을 제공합니다. 역할별로 사용 가능한 필터가 다릅니다. 정렬은 무조건 거리순입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4008\", \"data\": null, \"message\": \"거리순 정렬 시 위도/경도가 필요합니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "슬롯 규격 필터 권한 없음", value = "{\"code\": \"CF4033\", \"data\": null, \"message\": \"슬롯 규격 필터는 작가만 사용할 수 있습니다\"}"),
                                    @ExampleObject(name = "수수료율 필터 권한 없음", value = "{\"code\": \"CF4034\", \"data\": null, \"message\": \"수수료율 필터는 작가만 사용할 수 있습니다\"}"),
                                    @ExampleObject(name = "수수료율 정렬 권한 없음", value = "{\"code\": \"CF4035\", \"data\": null, \"message\": \"수수료율 정렬은 작가만 사용할 수 있습니다\"}")
                            }
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
    @GetMapping
    public ResponseEntity<ResponseDto<CafeListData>> searchCafes(
            @Parameter(description = "카페명 검색 키워드")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "위도", required = true)
            @RequestParam Double latitude,
            @Parameter(description = "경도", required = true)
            @RequestParam Double longitude,
            @Parameter(description = "슬롯 최소 폭 (cm)")
            @RequestParam(required = false) Integer minWidth,
            @Parameter(description = "슬롯 최대 폭 (cm)")
            @RequestParam(required = false) Integer maxWidth,
            @Parameter(description = "슬롯 최소 높이 (cm)")
            @RequestParam(required = false) Integer minHeight,
            @Parameter(description = "슬롯 최대 높이 (cm)")
            @RequestParam(required = false) Integer maxHeight,
            @Parameter(description = "최소 수수료율 (%)")
            @RequestParam(required = false) Double minCommission,
            @Parameter(description = "최대 수수료율 (%)")
            @RequestParam(required = false) Double maxCommission,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 거리 정보 포함
        List<CafeSummary> cafes = createMockCafes(MOCK_CAFE_COUNT, 200L, 0.5);

        Pagination pagination = new Pagination(page, size, (long) MOCK_CAFE_COUNT, (int) Math.ceil((double) MOCK_CAFE_COUNT / size), false);
        CafeListData data = new CafeListData(cafes, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "추천 카페 목록 조회", description = "특정 작품에 어울리는 카페 목록을 추천합니다. 알고리즘 기반으로 작품 특성과 카페 특성을 매칭합니다. 작품의 규격, 카테고리, 가격대 등을 고려합니다. 카페의 슬롯 규격, 수수료율, 위치, 인테리어 스타일 등을 고려합니다. 사용자 행동 데이터(북마크, 조회 이력 등)를 활용합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
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
    @GetMapping("/recommendations")
    public ResponseEntity<ResponseDto<CafeListData>> getRecommendedCafes(
            @Parameter(description = "작품 ID", required = true)
            @RequestParam Long artworkId,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터
        List<CafeSummary> cafes = createMockCafes(MOCK_CAFE_COUNT, 300L, null);

        Pagination pagination = new Pagination(page, size, (long) MOCK_CAFE_COUNT, (int) Math.ceil((double) MOCK_CAFE_COUNT / size), false);
        CafeListData data = new CafeListData(cafes, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 상세 조회", description = "특정 카페의 상세 정보를 조회합니다. 기본 정보, 이미지, 전시 슬롯, 슬롯별 전시 상태를 포함합니다. 조회수가 증가합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"data\": null, \"message\": \"존재하지 않는 카페입니다\"}"
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
    @GetMapping("/{cafeId}")
    public ResponseEntity<ResponseDto<CafeDetailData>> getCafeDetail(
            @Parameter(description = "조회할 카페 ID", required = true)
            @PathVariable Long cafeId
    ) {
        // Mock 데이터
        List<CafeImage> images = List.of(
                new CafeImage(1L, "https://example.com/cafe1.jpg", true, 1),
                new CafeImage(2L, "https://example.com/cafe2.jpg", false, 2),
                new CafeImage(3L, "https://example.com/cafe3.jpg", false, 3)
        );

        ExhibitionInfo exhibition = new ExhibitionInfo(
                1L, "여름의 여백", "https://example.com/artwork1.jpg",
                "홍길동", "2024-01-01", "2024-03-31"
        );

        List<SlotDetail> slots = List.of(
                new SlotDetail(1L, "1번 벽", 200, 250, "RAIL", 15.0,
                        "카페 입구 오른쪽 벽면", ExhibitionStatus.EXHIBITING, exhibition, "{}"),
                new SlotDetail(2L, "2번 벽", 150, 200, "HOOK", 12.0,
                        "창가 벽면", ExhibitionStatus.AVAILABLE, null, "{}"),
                new SlotDetail(3L, "3번 벽", 180, 220, "RAIL", 15.0,
                        "내부 중앙 벽면", ExhibitionStatus.SCHEDULED, null, "{}")
        );

        RatingStatsData ratingStats = new RatingStatsData(42, 4.5);
        CafeOwner owner = new CafeOwner(
                1L, "카페사장", "https://example.com/profile.jpg", ratingStats
        );

        CafeDetail cafeDetail = new CafeDetail(
                cafeId, "카페 여백", "서울시 강남구 테헤란로 123",
                "조용하고 아늑한 분위기의 카페입니다.", "02-1234-5678",
                "09:00", "22:00", "100~200명", 150, false,
                List.of("모던", "미니멀"), images, slots, owner
        );

        CafeDetailData data = new CafeDetailData(cafeDetail);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "카페 북마크 토글", description = "카페를 북마크에 추가하거나 제거합니다 (토글 방식). 모든 사용자가 사용 가능합니다. 북마크가 없으면 추가하고, 이미 있으면 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class)
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"CF4041\", \"data\": null, \"message\": \"존재하지 않는 카페입니다\"}"
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
    @PostMapping("/{cafeId}/bookmarks")
    public ResponseEntity<ResponseDto<BookmarkToggleData>> toggleBookmark(
            @Parameter(description = "북마크 토글할 카페 ID", required = true)
            @PathVariable Long cafeId
    ) {
        // Mock 데이터: 북마크 추가됨
        BookmarkToggleData data = new BookmarkToggleData(true);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    private List<CafeSummary> createMockCafes(int count, Long startId, Double baseDistance) {
        String[] names = {"카페 여백", "아트 카페", "갤러리 카페", "모던 카페", "빈티지 카페",
                "미니멀 카페", "북 카페", "오션뷰 카페", "숲속 카페", "루프탑 카페",
                "감성 카페", "복합문화 카페", "전시 카페", "작가의 카페", "예술 공간",
                "창작 카페", "디자인 카페", "뮤지엄 카페", "아틀리에 카페", "스튜디오 카페"};
        String[] addresses = {"서울시 강남구 테헤란로 123", "서울시 종로구 인사동길 45",
                "서울시 마포구 홍익로 78", "서울시 용산구 이태원로 234",
                "서울시 성동구 성수이로 156"};
        String[][] categoriesList = {
                {"모던", "미니멀"}, {"빈티지", "클래식"}, {"인더스트리얼", "모던"},
                {"북유럽", "미니멀"}, {"레트로", "빈티지"}
        };

        return IntStream.range(0, count)
                .mapToObj(i -> {
                    long id = startId + i;
                    String name = names[i % names.length] + " #" + id;
                    String address = addresses[i % addresses.length];
                    String primaryImageUrl = "https://example.com/cafe" + (i + 1) + ".jpg";
                    int availableSlotCount = 1 + (i % 4);
                    int totalSlotCount = 3 + (i % 3);
                    Double distance = baseDistance != null ? baseDistance + (i * 0.3) : null;
                    List<String> categories = List.of(categoriesList[i % categoriesList.length]);

                    return new CafeSummary(
                            id, name, address, primaryImageUrl,
                            availableSlotCount, totalSlotCount, distance, categories
                    );
                })
                .toList();
    }
}
