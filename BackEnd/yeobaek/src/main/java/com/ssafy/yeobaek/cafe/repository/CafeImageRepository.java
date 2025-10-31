package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.CafeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeImageRepository extends JpaRepository<CafeImage, Long> {
}
