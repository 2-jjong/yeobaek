package com.ssafy.yeobaek.user.service;

public interface UserService {

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 이미 존재하면 true, 사용 가능하면 false
     */
    boolean isEmailExists(String email);
}
