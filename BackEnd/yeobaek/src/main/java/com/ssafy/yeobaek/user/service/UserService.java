package com.ssafy.yeobaek.user.service;

import com.ssafy.yeobaek.user.entity.User;

import java.util.Optional;

public interface UserService {

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 이미 존재하면 true, 사용 가능하면 false
     */
    boolean isEmailExists(String email);

    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findByEmail(String email);

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 엔티티 (Optional)
     */
    Optional<User> findById(Long id);

    /**
     * 사용자 저장
     *
     * @param user 저장할 사용자 엔티티
     */
    void save(User user);
}
