package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisplayWallRepository extends JpaRepository<DisplayWall, Long> {
    boolean existsByCafe(Cafe cafe);
}
