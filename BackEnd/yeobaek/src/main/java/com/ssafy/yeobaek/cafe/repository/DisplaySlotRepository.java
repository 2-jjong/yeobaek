package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplaySlotRepository extends JpaRepository<DisplaySlot, Long> {
}
