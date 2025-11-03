package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.CafeCategoryTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeCategoryTagRepository extends JpaRepository<CafeCategoryTag, Long> {
}
