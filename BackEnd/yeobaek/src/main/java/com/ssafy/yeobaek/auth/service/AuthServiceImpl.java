package com.ssafy.yeobaek.auth.service;

import com.ssafy.yeobaek.auth.dto.response.EmailCheckData;
import com.ssafy.yeobaek.user.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserServiceImpl userServiceImpl;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return EmailCheckData (available: true면 사용 가능, false면 이미 사용 중)
     */
    public EmailCheckData checkEmailDuplicate(String email) {
        boolean exists = userServiceImpl.isEmailExists(email);
        boolean available = !exists; // exists의 반대 (존재하면 사용 불가)
        return new EmailCheckData(email, available);
    }
}
