package com.ssafy.yeobaek.user.service;

import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 이미 존재하면 true, 사용 가능하면 false
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
