package com.ssafy.yeobaek.user.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.user.dto.request.ChangePasswordRequest;
import com.ssafy.yeobaek.user.dto.request.DeleteAccountRequest;
import com.ssafy.yeobaek.user.dto.request.UpdateProfileRequest;
import com.ssafy.yeobaek.user.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다 / 만료된 토큰입니다"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<UserProfileData>> getMyProfile() {
        // Mock 데이터: ARTIST 사용자 프로필 반환
        UserData user = new UserData(
            1L,
            "artist@example.com",
            "작가 홍길동",
            "ARTIST",
            "https://example.com/artist-profile.jpg",
            "1990-01-01",
            "MALE"
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
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
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
            "ARTIST",
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
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "닉네임 길이 오류 / 작가 소개 길이 오류"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다 / 만료된 토큰입니다"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
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
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "비밀번호 형식 오류 / 비밀번호 불일치 / 필수 입력값 누락"),
        @ApiResponse(responseCode = "401", description = "현재 패스워드가 일치하지 않습니다 / 유효하지 않은 토큰 / 만료된 토큰"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
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
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "필수 입력값이 누락되었습니다"),
        @ApiResponse(responseCode = "401", description = "현재 패스워드가 일치하지 않습니다 / 유효하지 않은 토큰 / 만료된 토큰"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
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
