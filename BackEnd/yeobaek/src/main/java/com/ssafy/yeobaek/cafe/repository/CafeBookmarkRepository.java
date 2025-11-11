package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.CafeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CafeBookmarkRepository extends JpaRepository<CafeBookmark, Long> {

    /**
     * 특정 사용자가 특정 카페를 북마크했는지 확인
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     * @return 북마크 여부
     */
    boolean existsByUser_IdAndCafe_Id(Long userId, Long cafeId);

    /**
     * 사용자가 특정 카페를 북마크한 엔티티 삭제
     * @param userId 사용자 ID
     * @param cafeId 카페 ID
     */
    void deleteCafeBookmarkByUser_IdAndCafe_Id(Long userId, Long cafeId);
}
