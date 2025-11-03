package com.ssafy.yeobaek.user.service;

import com.ssafy.yeobaek.user.entity.User;
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
     * 사용자 저장
     *
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
}
