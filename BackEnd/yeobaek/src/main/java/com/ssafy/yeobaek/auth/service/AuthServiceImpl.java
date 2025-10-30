package com.ssafy.yeobaek.auth.service;

import com.ssafy.yeobaek.auth.dto.request.SignupRequest;
import com.ssafy.yeobaek.auth.dto.response.EmailCheckData;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.Gender;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.repository.UserRepository;
import com.ssafy.yeobaek.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService{

    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return EmailCheckData (available: true면 사용 가능, false면 이미 사용 중)
     */
    @Override
    public EmailCheckData checkEmailDuplicate(String email) {
        boolean exists = userServiceImpl.isEmailExists(email);
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
        if (userServiceImpl.isEmailExists(request.getEmail())) {
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
        userRepository.save(user);
    }
}
