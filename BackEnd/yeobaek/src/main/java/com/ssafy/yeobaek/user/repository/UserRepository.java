package com.ssafy.yeobaek.user.repository;

import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일 중복 체크
     * @param email 확인할 이메일
     * @return 이메일이 존재하면 true, 아니면 false
     */
    boolean existsByEmail(String email);
}
