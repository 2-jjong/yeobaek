package com.ssafy.yeobaek.auth.service;

import com.ssafy.yeobaek.auth.dto.request.SignupRequest;
import com.ssafy.yeobaek.auth.dto.response.EmailCheckData;

public interface AuthService {

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return EmailCheckData (available: true면 사용 가능, false면 이미 사용 중)
     */
    EmailCheckData checkEmailDuplicate(String email);

    /**
     * 회원가입
     * @param request 회원가입 요청 정보
     */
    void signup(SignupRequest request);
}
