package com.ssafy.yeobaek.review.mapper;

import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.dto.response.RatingStat;
import com.ssafy.yeobaek.review.dto.response.ReviewStatistics;
import com.ssafy.yeobaek.review.entity.Review;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Review 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class ReviewMapper {

    /**
     * CreateReviewRequest DTO를 Review 엔티티로 변환
     * @param request CreateReviewRequest DTO
     * @param reviewer 리뷰 작성자 (User)
     * @param reviewee 리뷰 대상자 (User)
     * @param exhibition 전시 (Exhibition)
     * @return Review 엔티티
     */
    public Review toEntity(CreateReviewRequest request, User reviewer, User reviewee, Exhibition exhibition) {
        return Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .exhibition(exhibition)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    /**
     * 통계 데이터를 ReviewStatistics DTO로 변환
     * @param totalReviewCount 전체 리뷰 수
     * @param totalReviewScore 평균 평점
     * @param ratingStats 별점별 통계 리스트
     * @return 평가 통계 DTO
     */
    public ReviewStatistics toReviewStatistics(int totalReviewCount, double totalReviewScore, List<RatingStat> ratingStats) {
        return ReviewStatistics.builder()
                .totalReviewCount(totalReviewCount)
                .totalReviewScore(totalReviewScore)
                .ratingStats(ratingStats)
                .build();
    }

    /**
     * 별점 통계 데이터를 RatingStat DTO로 변환
     * @param rating 별점
     * @param count 개수
     * @param percentage 비율
     * @return 별점 통계 DTO
     */
    public RatingStat toRatingStat(int rating, int count, double percentage) {
        return RatingStat.builder()
                .rating(rating)
                .count(count)
                .percentage(percentage)
                .build();
    }
}
