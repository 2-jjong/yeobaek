package com.ssafy.yeobaek.auth.service;

import com.ssafy.yeobaek.auth.dto.request.LoginRequest;
import com.ssafy.yeobaek.auth.dto.request.SignupRequest;
import com.ssafy.yeobaek.auth.dto.response.EmailCheckData;
import com.ssafy.yeobaek.auth.dto.response.LoginData;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.Gender;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.common.util.JwtUtil;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService{

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return EmailCheckData (available: true면 사용 가능, false면 이미 사용 중)
     */
    @Override
    public EmailCheckData checkEmailDuplicate(String email) {
        boolean exists = userService.isEmailExists(email);
        boolean available = !exists; // exists의 반대 (존재하면 사용 불가)
        return new EmailCheckData(email, available);
    }

    /**
     * 회원가입
     * @param request 회원가입 요청 정보
     */
    @Override
    @Transactional
    public void signup(SignupRequest request) {
        // 이메일 중복 검증
        if (userService.isEmailExists(request.getEmail())) {
            throw new CustomException(ResponseCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 일치 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ResponseCode.PASSWORD_MISMATCH);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .userType(UserRole.valueOf(request.getUserType()))
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .birthDate(LocalDate.parse(request.getBirthDate()))
                .gender(Gender.valueOf(request.getGender()))
                .build();

        // DB 저장
        userService.save(user);
    }

    /**
     * 로그인
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return LoginData (accessToken, refreshToken, userType)
     */
    @Override
    public LoginData login(LoginRequest request) {
        // 이메일로 사용자 조회
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ResponseCode.INVALID_CREDENTIALS);
        }

        // JWT Access Token 생성 (userId, userType 포함)
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserType());

        // JWT Refresh Token 생성 (userId만 포함)
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Refresh Token을 Redis에 저장
        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        // LoginData 반환
        return new LoginData(accessToken, refreshToken, user.getUserType());
    }

    /**
     * 로그아웃
     * - Redis에서 Refresh Token 삭제
     */
    @Override
    public void logout() {
        // 현재 인증된 사용자 ID 조회
        Long userId = SecurityUtil.getCurrentUserId();

        // Redis에서 Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId);
    }
}
