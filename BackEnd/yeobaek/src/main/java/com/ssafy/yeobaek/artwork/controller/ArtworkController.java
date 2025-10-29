package com.ssafy.yeobaek.artwork.controller;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.request.UpdateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.*;
import com.ssafy.yeobaek.common.dto.ResponseDto;
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

@Tag(name = "작품 관리", description = "작품 관리 API")
@RestController
@RequestMapping("/api/v1/artworks")
public class ArtworkController {

    private final int MOCK_ARTWORK_COUNT = 20;

    @Operation(summary = "작품 등록", description = "작가가 피드 형식으로 작품을 등록합니다. 이미지는 최소 1장, 최대 10장까지 슬라이드 형식으로 등록 가능합니다.")
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
                                    @ExampleObject(name = "최소 이미지 개수 미달", value = "{\"code\": \"AW4001\", \"data\": null, \"message\": \"이미지는 최소 1장 이상 등록해야 합니다\"}"),
                                    @ExampleObject(name = "최대 이미지 개수 초과", value = "{\"code\": \"AW4002\", \"data\": null, \"message\": \"이미지는 최대 10장까지 등록 가능합니다\"}"),
                                    @ExampleObject(name = "작품명 길이 오류", value = "{\"code\": \"AW4003\", \"data\": null, \"message\": \"작품명은 1~100자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "작품 설명 길이 오류", value = "{\"code\": \"AW4004\", \"data\": null, \"message\": \"작품 설명은 최대 1000자까지 입력 가능합니다\"}"),
                                    @ExampleObject(name = "유효하지 않은 카테고리", value = "{\"code\": \"AW4006\", \"data\": null, \"message\": \"유효하지 않은 카테고리입니다\"}"),
                                    @ExampleObject(name = "대표 이미지 개수 오류", value = "{\"code\": \"AW4008\", \"data\": null, \"message\": \"대표 이미지는 1개만 선택해야 합니다\"}"),
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
                                    value = "{\"code\": \"AW4031\", \"data\": null, \"message\": \"작가만 작품을 등록할 수 있습니다\"}"
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
    public ResponseEntity<ResponseDto<Void>> createArtwork(@RequestBody CreateArtworkRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.created());
    }

    @Operation(summary = "작품 수정", description = "등록된 작품 정보를 수정합니다. 전시 중인 작품은 일부 정보만 수정 가능합니다.")
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
                                    @ExampleObject(name = "최소 이미지 개수 미달", value = "{\"code\": \"AW4001\", \"data\": null, \"message\": \"이미지는 최소 1장 이상 등록해야 합니다\"}"),
                                    @ExampleObject(name = "최대 이미지 개수 초과", value = "{\"code\": \"AW4002\", \"data\": null, \"message\": \"이미지는 최대 10장까지 등록 가능합니다\"}"),
                                    @ExampleObject(name = "작품명 길이 오류", value = "{\"code\": \"AW4003\", \"data\": null, \"message\": \"작품명은 1~100자 이내여야 합니다\"}"),
                                    @ExampleObject(name = "작품 설명 길이 오류", value = "{\"code\": \"AW4004\", \"data\": null, \"message\": \"작품 설명은 최대 1000자까지 입력 가능합니다\"}"),
                                    @ExampleObject(name = "유효하지 않은 카테고리", value = "{\"code\": \"AW4006\", \"data\": null, \"message\": \"유효하지 않은 카테고리입니다\"}"),
                                    @ExampleObject(name = "대표 이미지 개수 오류", value = "{\"code\": \"AW4008\", \"data\": null, \"message\": \"대표 이미지는 1개만 선택해야 합니다\"}")
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
                                    value = "{\"code\": \"AW4091\", \"data\": null, \"message\": \"전시 중인 작품은 규격을 변경할 수 없습니다\"}"
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
    @PutMapping("/{artworkId}")
    public ResponseEntity<ResponseDto<Void>> updateArtwork(
            @Parameter(description = "수정할 작품 ID", required = true)
            @PathVariable Long artworkId,
            @RequestBody UpdateArtworkRequest request
    ) {
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
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"AU4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}")
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
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<ArtworkListData>> getMyArtworks(
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 20개로 증가
        List<ArtworkSummary> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 1L);

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListData data = new ArtworkListData(null, artworks, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "특정 작가의 작품 목록 조회", description = "특정 작가의 작품 목록을 조회합니다.")
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
                            examples = @ExampleObject(value = "{\"code\": \"AU4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}")
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
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseDto<ArtworkListData>> getUserArtworks(
            @Parameter(description = "작가 회원 ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 20개로 증가
        List<ArtworkSummary> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 100L); // 다른 ID로 시작

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListData data = new ArtworkListData(null, artworks, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "작가/작품 목록 조회", description = "작품 목록을 조회하거나 검색합니다. 키워드 검색, 필터링, 정렬 기능을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"AW4007\", \"data\": null, \"message\": \"작품 규격 필터는 카페 사장만 사용할 수 있습니다\"}")
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
    @GetMapping
    public ResponseEntity<ResponseDto<ArtworkListData>> searchArtworks(
            @Parameter(description = "작품명, 작가명 검색 키워드")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "카테고리 필터 (다중 선택)")
            @RequestParam(required = false) List<String> categories,
            @Parameter(description = "작품 최소 가로 (cm)")
            @RequestParam(required = false) Integer minWidth,
            @Parameter(description = "작품 최대 가로 (cm)")
            @RequestParam(required = false) Integer maxWidth,
            @Parameter(description = "작품 최소 세로 (cm)")
            @RequestParam(required = false) Integer minHeight,
            @Parameter(description = "작품 최대 세로 (cm)")
            @RequestParam(required = false) Integer maxHeight,
            @Parameter(description = "정렬 기준 (POPULARITY: 인기순, LATEST: 최신 등록순)")
            @RequestParam(defaultValue = "LATEST") String sortBy,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터
        ArtistSummary artist1 = new ArtistSummary(
                1L, "홍길동", "https://example.com/profile1.jpg",
                "현대 미술을 전공한 신진 작가입니다.", 15
        );

        // Mock 데이터 - 20개로 증가
        List<ArtworkSummary> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 200L); // 다른 ID로 시작

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListData data = new ArtworkListData(List.of(artist1), artworks, pagination);

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
    public ResponseEntity<ResponseDto<ArtworkListData>> getRecommendedArtworks(
            @Parameter(description = "카페 ID", required = true)
            @RequestParam Long cafeId,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 20개로 증가
        List<ArtworkSummary> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 300L); // 다른 ID로 시작

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListData data = new ArtworkListData(null, artworks, pagination);

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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"AW4041\", \"data\": null, \"message\": \"존재하지 않는 작품입니다\"}")
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
    @GetMapping("/{artworkId}")
    public ResponseEntity<ResponseDto<ArtworkDetailData>> getArtworkDetail(
            @Parameter(description = "조회할 작품 ID", required = true)
            @PathVariable Long artworkId
    ) {
        // Mock 데이터 (단일 작품)
        List<ArtworkImage> images = List.of(
                new ArtworkImage(1L, "https://example.com/image1.jpg", 1),
                new ArtworkImage(2L, "https://example.com/image2.jpg", 2)
        );

        Artist artist = new Artist(
                1L, "홍길동", "https://example.com/profile.jpg",
                "현대 미술을 전공한 신진 작가입니다."
        );

        Exhibition exhibition = new Exhibition(
                1L, "카페 여백", "서울시 강남구 테헤란로 123",
                "https://example.com/cafe.jpg", "메인 전시 공간",
                "2024-01-01", "2024-03-31"
        );

        ArtworkDetail artwork = new ArtworkDetail(
                artworkId, "여름의 여백", "여름날 햇살이 비치는 풍경을 표현한 작품입니다.",
                500000, "풍경화", 80, 60, 2.5, 2024,
                "EXHIBITING", 150, 42, false, images, artist, exhibition
        );

        ArtworkDetailData data = new ArtworkDetailData(artwork);

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
    public ResponseEntity<ResponseDto<PopularArtworksData>> getPopularArtworks(
            @Parameter(description = "조회할 작품 수 (기본값: 10, 최대: 20)")
            @RequestParam(defaultValue = "10") Integer size
    ) {
        // Mock 데이터 - size에 따라 최대 20개까지 생성
        int count = Math.min(size, MOCK_ARTWORK_COUNT);
        List<ArtworkSummary> artworks = createMockArtworks(count, 400L);

        PopularArtworksData data = new PopularArtworksData(artworks);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(summary = "작품 북마크 토글", description = "작품을 북마크에 추가하거나 제거합니다 (토글 방식). 북마크가 없으면 추가하고, 이미 있으면 제거합니다.")
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
                    responseCode = "404",
                    description = "리소스 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"code\": \"AW4041\", \"data\": null, \"message\": \"존재하지 않는 작품입니다\"}")
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
    @PostMapping("/{artworkId}/bookmarks")
    public ResponseEntity<ResponseDto<BookmarkToggleData>> toggleBookmark(
            @Parameter(description = "북마크 토글할 작품 ID", required = true)
            @PathVariable Long artworkId
    ) {
        // Mock 데이터: 북마크 추가됨
        BookmarkToggleData data = new BookmarkToggleData(true);

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
    public ResponseEntity<ResponseDto<ArtworkListData>> getBookmarkedArtworks(
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값: 0)")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "페이지 크기 (기본값: 20, 최대: 50)")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        // Mock 데이터 - 20개로 증가
        List<ArtworkSummary> artworks = createMockArtworks(MOCK_ARTWORK_COUNT, 500L);

        // 전체 개수를 MOCK_ARTWORK_COUNT(20)으로 설정
        Pagination pagination = new Pagination(page, size, (long) MOCK_ARTWORK_COUNT, (int) Math.ceil((double) MOCK_ARTWORK_COUNT / size), false);
        ArtworkListData data = new ArtworkListData(null, artworks, pagination);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    private List<ArtworkSummary> createMockArtworks(int count, Long startId) {
        String[] titles = {"여름의 여백", "가을의 정적", "봄날의 꿈", "겨울의 침묵", "도시의 밤", "산속의 오두막", "추상의 세계", "바다의 속삭임", "황혼의 노래", "새벽의 기도", "꽃병과 과일", "푸른 하늘", "고요한 아침", "빛과 그림자", "시간의 흐름", "미지의 공간", "노을 진 풍경", "밤하늘의 별", "꿈속의 정원", "자유로운 영혼"};
        String[] categories = {"풍경화", "인물화", "정물화", "장르화", "기타", "역사화"};
        String[] statuses = {"AVAILABLE", "EXHIBITING"};
        String[] exhibitLocations = {null, "카페 여백", "아트카페", "오션뷰 카페", null};

        return IntStream.range(0, count)
                .mapToObj(i -> {
                    long id = startId + i;
                    String title = titles[i % titles.length] + " #" + id;
                    String image = "https://example.com/artwork" + (i + 1) + ".jpg";
                    int price = 400000 + (i * 50000) % 500000;
                    String category = categories[i % categories.length];
                    int width = 60 + (i * 5) % 60;
                    int height = 70 + (i * 5) % 50;
                    String status = statuses[i % statuses.length];
                    int views = 30 + (i * 10);
                    String location = exhibitLocations[i % exhibitLocations.length];
                    String artistName = (id % 3 == 0) ? "김작가" : "이작가";
                    String artistProfile = "https://example.com/profile" + (id % 2 + 1) + ".jpg";

                    return new ArtworkSummary(
                            id, title, image,
                            artistName, artistProfile,
                            price, category, width, height, status, views, location
                    );
                })
                .toList();
    }
}