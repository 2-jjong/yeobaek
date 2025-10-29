package com.ssafy.yeobaek.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "회원가입 요청")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @Schema(description = "회원 유형 (CAFE_OWNER, ARTIST, GENERAL)", example = "GENERAL", required = true)
    @NotBlank(message = "회원 유형은 필수 입력값입니다")
    @Pattern(regexp = "^(CAFE_OWNER|ARTIST|GENERAL)$", message = "유효하지 않은 회원 유형입니다")
    private String userType;

    @Schema(description = "로그인용 이메일 주소", example = "user@example.com", required = true)
    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문+숫자+특수문자)", example = "Password123!", required = true)
    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
             message = "비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다")
    private String password;

    @Schema(description = "비밀번호 확인", example = "Password123!", required = true)
    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다")
    private String passwordConfirm;

    @Schema(description = "닉네임 (카페 사장: 카페 상호명, 작가: 작가명, 일반: 닉네임)", example = "홍길동", required = true)
    @NotBlank(message = "닉네임은 필수 입력값입니다")
    @Size(min = 2, max = 15, message = "닉네임은 2~15자 이내여야 합니다")
    private String nickname;

    @Schema(description = "생년월일 (YYYY-MM-DD 형식)", example = "1990-01-01", required = true)
    @NotBlank(message = "생년월일은 필수 입력값입니다")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일 형식이 올바르지 않습니다 (YYYY-MM-DD)")
    private String birthDate;

    @Schema(description = "성별 (MALE, FEMALE)", example = "MALE", required = true)
    @NotBlank(message = "성별은 필수 입력값입니다")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "유효하지 않은 성별입니다")
    private String gender;
}
