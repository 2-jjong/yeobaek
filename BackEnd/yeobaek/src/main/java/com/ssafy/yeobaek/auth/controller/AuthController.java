package com.ssafy.yeobaek.auth.controller;

import com.ssafy.yeobaek.auth.dto.request.*;
import com.ssafy.yeobaek.auth.dto.response.*;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag(name = "회원 관리", description = "회원 인증 및 관리 API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Operation(summary = "이메일 중복 검증", description = "회원가입 전 이메일 중복 여부를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "이메일 형식이 올바르지 않습니다 / 필수 입력값이 누락되었습니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/email/check")
    public ResponseEntity<ResponseDto<EmailCheckData>> checkEmail(@Valid @RequestBody EmailCheckRequest request) {
        // Mock 데이터: 사용 가능한 이메일로 반환
        EmailCheckData data = new EmailCheckData(request.getEmail(), true);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success(data));
    }

    @Operation(summary = "회원가입", description = "사용자 계정을 생성하고 기본 정보를 등록합니다. userType에 따라 카페 사장, 작가, 일반 사용자로 구분됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "이미 사용 중인 이메일입니다 / 비밀번호 형식 오류 / 비밀번호 불일치 등"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Void>> signup(@Valid @RequestBody SignupRequest request) {
        // Mock 데이터: 성공 응답 반환 (data는 null)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseDto.created());
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 인증하고 JWT 토큰을 발급합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "필수 입력값이 누락되었습니다"),
        @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호가 일치하지 않습니다"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginData>> login(@Valid @RequestBody LoginRequest request) {
        // Mock 데이터: JWT 토큰 반환
        LoginData data = new LoginData(
            "mock.access.token.jwt.string",
            "mock.refresh.token.jwt.string",
            "GENERAL"
        );
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success(data));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다. 서버에서 해당 사용자의 refreshToken을 무효화합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다 / 만료된 토큰입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/logout")
    public ResponseEntity<ResponseDto<Void>> logout(
        @Parameter(description = "Bearer {accessToken}", required = true)
        @RequestHeader("Authorization") String authorization
    ) {
        // Mock 데이터: 성공 응답 반환 (data는 null)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success());
    }

    @Operation(summary = "Access Token 재발급", description = "refreshToken을 사용하여 새로운 accessToken과 refreshToken을 발급합니다. 토큰 갱신(rotate) 방식으로 refreshToken도 함께 갱신됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다 / 만료된 토큰입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<TokenData>> refresh(
        @Parameter(description = "Bearer {refreshToken}", required = true)
        @RequestHeader("Authorization") String authorization
    ) {
        // Mock 데이터: 새로운 토큰 반환
        TokenData data = new TokenData(
            "new.mock.access.token.jwt.string",
            "new.mock.refresh.token.jwt.string"
        );
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success(data));
    }

    @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정을 위한 이메일 인증 링크를 발송합니다. 인증 링크는 30분간 유효합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "이메일 형식이 올바르지 않습니다 / 필수 입력값이 누락되었습니다"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/password/reset-request")
    public ResponseEntity<ResponseDto<PasswordResetRequestData>> passwordResetRequest(
        @Valid @RequestBody PasswordResetRequestRequest request
    ) {
        // Mock 데이터: 이메일과 만료시간 반환
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        PasswordResetRequestData data = new PasswordResetRequestData(
            request.getEmail(),
            expiresAt.format(formatter)
        );
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success(data));
    }

    @Operation(summary = "비밀번호 재설정", description = "이메일 인증 후 새로운 비밀번호로 재설정합니다. 재설정 토큰은 30분간 유효합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상 처리"),
        @ApiResponse(responseCode = "400", description = "비밀번호 형식 오류 / 비밀번호 불일치 / 필수 입력값 누락"),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다 / 만료된 토큰입니다"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다")
    })
    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDto<Void>> passwordReset(@Valid @RequestBody PasswordResetRequest request) {
        // Mock 데이터: 성공 응답 반환 (data는 null)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ResponseDto.success());
    }
}
