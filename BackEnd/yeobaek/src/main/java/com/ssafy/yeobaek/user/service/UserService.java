package com.ssafy.yeobaek.user.service;

import com.ssafy.yeobaek.user.dto.request.UpdateProfileRequest;
import com.ssafy.yeobaek.user.dto.response.UserProfileData;
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

    /**
     * 내 프로필 조회
     * @param userId 사용자 ID
     * @return 사용자 프로필 데이터
     */
    UserProfileData getMyProfile(Long userId);

    /**
     * 특정 사용자 공개 프로필 조회
     * @param userId 조회할 사용자 ID
     * @return 공개 프로필 데이터
     */
    UserProfileData getUserProfile(Long userId);

    /**
     * 프로필 정보 수정
     * @param userId 사용자 ID
     * @param request 수정할 프로필 정보
     */
    void updateProfile(Long userId, UpdateProfileRequest request);
}
