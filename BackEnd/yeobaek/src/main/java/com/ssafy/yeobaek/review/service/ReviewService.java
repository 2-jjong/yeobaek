package com.ssafy.yeobaek.review.service;

import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;

public interface ReviewService {

    /**
     * 전시 리뷰 작성
     * @param userId 리뷰 작성자 ID
     * @param request 리뷰 작성 요청
     */
    void createReview(Long userId, CreateReviewRequest request);
}
