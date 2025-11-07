package com.ssafy.yeobaek.review.service;

import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.dto.response.ReviewStatistics;

public interface ReviewService {

    /**
     * 전시 리뷰 작성
     * @param userId 리뷰 작성자 ID
     * @param request 리뷰 작성 요청
     */
    void createReview(Long userId, CreateReviewRequest request);

    /**
     * 내가 받은 평가 통계 조회
     * @param userId 사용자 ID
     * @return 평가 통계
     */
    ReviewStatistics getMyReviewStatistics(Long userId);
}
