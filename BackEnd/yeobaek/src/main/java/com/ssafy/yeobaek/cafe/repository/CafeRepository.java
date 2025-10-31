package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long> {
    boolean existsByCafeOwner(User cafeOwner);
}
