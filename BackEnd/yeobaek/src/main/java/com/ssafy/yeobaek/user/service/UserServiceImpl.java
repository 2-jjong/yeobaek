package com.ssafy.yeobaek.user.service;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.dto.request.UpdateProfileRequest;
import com.ssafy.yeobaek.user.dto.response.ArtistData;
import com.ssafy.yeobaek.user.dto.response.CafeData;
import com.ssafy.yeobaek.user.dto.response.UserData;
import com.ssafy.yeobaek.user.dto.response.UserProfileData;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.mapper.UserMapper;
import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 이미 존재하면 true, 사용 가능하면 false
     */
    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 이메일로 사용자 조회
     * @param email 조회할 이메일
     * @return 사용자 엔티티 (Optional)
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return 사용자 엔티티 (Optional)
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 사용자 저장
     * @param user 저장할 사용자 엔티티
     */
    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    /**
     * id로 사용자 조회
     * @param id 조회할 id
     * @return 사용자 엔티티 (Optional)
     */
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 내 프로필 조회
     * @param userId 사용자 ID
     * @return 사용자 프로필 데이터
     */
    @Override
    public UserProfileData getMyProfile(Long userId) {
        // 사용자 조회
        User user = findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 사용자 기본 정보 변환
        UserData userData = userMapper.toUserData(user);

        // UserType에 따라 추가 정보 생성
        ArtistData artistData = user.getUserType() == UserRole.ARTIST
                ? userMapper.toDummyArtistData()
                : null;

        CafeData cafeData = user.getUserType() == UserRole.CAFE_OWNER
                ? userMapper.toDummyCafeData()
                : null;

        return UserProfileData.builder()
                .user(userData)
                .artist(artistData)
                .cafe(cafeData)
                .build();
    }

    /**
     * 프로필 정보 수정
     * @param userId 사용자 ID
     * @param request 수정할 프로필 정보
     */
    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        // 사용자 조회
        User user = findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 닉네임 수정
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        // 프로필 이미지 URL 수정
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        // TODO: Artist 엔티티 및 Repository 구현 후 작가 소개(introduction) 수정 기능 추가
        // if (user.getUserType() == UserRole.ARTIST && request.getIntroduction() != null) {
        //     Artist artist = artistRepository.findByUserId(userId)
        //             .orElseThrow(() -> new CustomException(ResponseCode.ARTIST_NOT_FOUND));
        //     artist.setIntroduction(request.getIntroduction());
        // }

        // 변경 사항 저장
        userRepository.save(user);
    }
}
