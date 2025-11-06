package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CafeRepository extends JpaRepository<Cafe, Long> {
    boolean existsByCafeOwner(User user);

    Optional<Cafe> findByCafeOwner(User user);
}
