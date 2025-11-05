package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayWallRepository extends JpaRepository<DisplayWall, Long> {
}
