package com.ssafy.yeobaek.review.repository;

import com.ssafy.yeobaek.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 리뷰어와 전시로 리뷰 존재 여부 확인
     * @param reviewerId 리뷰 작성자 ID
     * @param exhibitionId 전시 ID
     * @return 리뷰 존재 여부
     */
    boolean existsByReviewerIdAndExhibitionId(Long reviewerId, Long exhibitionId);

    /**
     * 특정 사용자가 받은 모든 리뷰 조회
     * @param revieweeId 리뷰를 받은 사용자 ID
     * @return 리뷰 목록
     */
    List<Review> findAllByRevieweeId(Long revieweeId);
}
