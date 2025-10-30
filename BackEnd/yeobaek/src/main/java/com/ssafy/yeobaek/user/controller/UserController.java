package com.ssafy.yeobaek.user.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.common.enums.Gender;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.user.dto.request.ChangePasswordRequest;
import com.ssafy.yeobaek.user.dto.request.DeleteAccountRequest;
import com.ssafy.yeobaek.user.dto.request.UpdateProfileRequest;
import com.ssafy.yeobaek.user.dto.response.*;
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

@Tag(name = "사용자 프로필", description = "사용자 프로필 관리 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다. userType에 따라 반환되는 정보가 다릅니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "정상 처리",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 1, \"email\": \"artist@example.com\", \"nickname\": \"작가 홍길동\", \"userType\": \"ARTIST\", \"profileImageUrl\": \"https://example.com/artist-profile.jpg\", \"birthDate\": \"1990-01-01\", \"gender\": \"MALE\"}, \"artist\": {\"introduction\": \"현대 미술을 전공한 신진 작가입니다.\", \"totalExhibitions\": 15, \"totalArtworks\": 5, \"ratingStats\": {\"totalReviews\": 42, \"averageRating\": 4.5}}, \"cafeOwner\": null}, \"message\": \"정상 처리\"}"
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
                        name = "유효하지 않은 토큰",
                        value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                    ),
                    @ExampleObject(
                        name = "만료된 토큰",
                        value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                )
            )
        )
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserProfileData>> getMyProfile() {
        // Mock 데이터: ARTIST 사용자 프로필 반환
        UserData user = new UserData(
            1L,
            "artist@example.com",
            "작가 홍길동",
            UserRole.ARTIST,
            "https://example.com/artist-profile.jpg",
            "1990-01-01",
            Gender.MALE
        );

        RatingStatsData ratingStats = new RatingStatsData(42, 4.5);
        ArtistData artist = new ArtistData(
            "현대 미술을 전공한 신진 작가입니다. 주로 추상화와 설치미술 작업을 하고 있습니다.",
            15,
            5,
            ratingStats
        );

        UserProfileData data = new UserProfileData(user, artist, null);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success(data));
    }

    @Operation(summary = "특정 사용자 프로필 조회", description = "특정 사용자의 공개 프로필 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "정상 처리",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ResponseDto.class),
                examples = @ExampleObject(
                    value = "{\"code\": \"S0000\", \"data\": {\"user\": {\"userId\": 1, \"email\": null, \"nickname\": \"작가명\", \"userType\": \"ARTIST\", \"profileImageUrl\": \"https://example.com/artist.jpg\", \"birthDate\": null, \"gender\": null}, \"artist\": {\"introduction\": \"현대 미술을 전공한 신진 작가입니다.\", \"totalExhibitions\": 15, \"totalArtworks\": 5, \"ratingStats\": {\"totalReviews\": 42, \"averageRating\": 4.5}}, \"cafeOwner\": null}, \"message\": \"정상 처리\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                )
            )
        )
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<UserProfileData>> getUserProfile(
        @Parameter(description = "조회할 회원 ID", required = true)
        @PathVariable Long userId
    ) {
        // Mock 데이터: ARTIST 사용자 프로필 반환
        UserData user = new UserData(
            userId,
            null, // 이메일은 공개 프로필에서 제외
            "작가명",
            UserRole.ARTIST,
            "https://example.com/artist.jpg",
            null, // 생년월일은 공개 프로필에서 제외
            null  // 성별은 공개 프로필에서 제외
        );

        RatingStatsData ratingStats = new RatingStatsData(42, 4.5);
        ArtistData artist = new ArtistData(
            "현대 미술을 전공한 신진 작가입니다.",
            15,
            5,
            ratingStats
        );

        UserProfileData data = new UserProfileData(user, artist, null);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success(data));
    }

    @Operation(summary = "프로필 정보 수정", description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.")
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
                    @ExampleObject(
                        name = "닉네임 길이 오류",
                        value = "{\"code\": \"US4002\", \"data\": null, \"message\": \"닉네임은 2자 이상 15자 이하이어야 합니다\"}"
                    ),
                    @ExampleObject(
                        name = "작가 소개 길이 오류",
                        value = "{\"code\": \"US4003\", \"data\": null, \"message\": \"작가 소개는 250자 이하이어야 합니다\"}"
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
                        name = "유효하지 않은 토큰",
                        value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                    ),
                    @ExampleObject(
                        name = "만료된 토큰",
                        value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                )
            )
        )
    })
    @PutMapping("/me")
    public ResponseEntity<ResponseDto<Void>> updateProfile(
        @RequestBody UpdateProfileRequest request
    ) {
        // Mock 데이터: 성공 응답 반환
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success());
    }

    @Operation(summary = "패스워드 변경", description = "현재 로그인한 사용자의 패스워드를 변경합니다.")
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
                    @ExampleObject(
                        name = "비밀번호 형식 오류",
                        value = "{\"code\": \"AU4002\", \"data\": null, \"message\": \"비밀번호는 8자 이상 20자 이하이어야 합니다\"}"
                    ),
                    @ExampleObject(
                        name = "비밀번호 불일치",
                        value = "{\"code\": \"AU4003\", \"data\": null, \"message\": \"비밀번호가 일치하지 않습니다\"}"
                    ),
                    @ExampleObject(
                        name = "필수 입력값 누락",
                        value = "{\"code\": \"S4001\", \"data\": null, \"message\": \"필수 입력값이 누락되었습니다\"}"
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
                        name = "현재 패스워드 불일치",
                        value = "{\"code\": \"US4011\", \"data\": null, \"message\": \"현재 패스워드가 일치하지 않습니다\"}"
                    ),
                    @ExampleObject(
                        name = "유효하지 않은 토큰",
                        value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                    ),
                    @ExampleObject(
                        name = "만료된 토큰",
                        value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                )
            )
        )
    })
    @PutMapping("/me/password")
    public ResponseEntity<ResponseDto<Void>> changePassword(
        @RequestBody ChangePasswordRequest request
    ) {
        // Mock 데이터: 성공 응답 반환
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success());
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다. 회원 관련 데이터가 모두 삭제되며, 복구할 수 없습니다.")
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
                examples = @ExampleObject(
                    name = "필수 입력값 누락",
                    value = "{\"code\": \"S4001\", \"data\": null, \"message\": \"필수 입력값이 누락되었습니다\"}"
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
                        name = "현재 패스워드 불일치",
                        value = "{\"code\": \"US4011\", \"data\": null, \"message\": \"현재 패스워드가 일치하지 않습니다\"}"
                    ),
                    @ExampleObject(
                        name = "유효하지 않은 토큰",
                        value = "{\"code\": \"AU4012\", \"data\": null, \"message\": \"유효하지 않은 토큰입니다\"}"
                    ),
                    @ExampleObject(
                        name = "만료된 토큰",
                        value = "{\"code\": \"AU4013\", \"data\": null, \"message\": \"만료된 토큰입니다\"}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 회원",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"US4041\", \"data\": null, \"message\": \"존재하지 않는 회원입니다\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"code\": \"S5000\", \"data\": null, \"message\": \"서버 내부 오류가 발생했습니다\"}"
                )
            )
        )
    })
    @DeleteMapping("/me")
    public ResponseEntity<ResponseDto<Void>> deleteAccount(
        @RequestBody DeleteAccountRequest request
    ) {
        // Mock 데이터: 성공 응답 반환
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success());
    }
}
