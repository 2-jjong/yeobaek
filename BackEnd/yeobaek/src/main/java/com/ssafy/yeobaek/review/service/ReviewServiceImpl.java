package com.ssafy.yeobaek.review.service;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.review.dto.request.CreateReviewRequest;
import com.ssafy.yeobaek.review.entity.Review;
import com.ssafy.yeobaek.review.mapper.ReviewMapper;
import com.ssafy.yeobaek.review.repository.ReviewRepository;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ExhibitionRepository exhibitionRepository;
    private final ReviewMapper reviewMapper;

    /**
     * 전시 리뷰 작성
     * @param userId 리뷰 작성자 ID
     * @param request 리뷰 작성 요청
     */
    @Override
    @Transactional
    public void createReview(Long userId, CreateReviewRequest request) {
        log.info("리뷰 작성 시작 - 작성자 ID: {}, 전시 ID: {}, 대상자 ID: {}, 평점: {}, 후기: {}",
                userId, request.getExhibitionId(), request.getRevieweeId(), request.getRating(), request.getComment());

        // 기존 리뷰 존재 여부 확인 (중복 체크를 먼저 수행)
        if (reviewRepository.existsByReviewerIdAndExhibitionId(userId, request.getExhibitionId())) {
            log.warn("중복 리뷰 작성 시도 - 작성자 ID: {}, 전시 ID: {}", userId, request.getExhibitionId());
            throw new CustomException(ResponseCode.REVIEW_ALREADY_EXISTS);
        }

        // 리뷰 작성자 조회
        User reviewer = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // 리뷰 대상자 조회
        User reviewee = userService.findById(request.getRevieweeId())
                .orElseThrow(() -> new CustomException(ResponseCode.PROFILE_USER_NOT_FOUND));

        // TODO: 추후 Exhibition 구현 되면 Service로 의존성 변경, 실제 전시 유저(카페 사장, 작가)인지 검증 필요
        // 전시 조회
        Exhibition exhibition = exhibitionRepository.findById(request.getExhibitionId())
                .orElseThrow(() -> new CustomException(ResponseCode.EXHIBITION_NOT_FOUND));

        // Mapper를 사용해서 Review 엔티티로 변환
        Review review = reviewMapper.toEntity(request, reviewer, reviewee, exhibition);

        // 저장
        reviewRepository.save(review);
        log.info("리뷰 작성 완료 - 리뷰 ID: {}, 작성자: {}, 대상자: {}, 전시 ID: {}",
                review.getId(), reviewer.getNickname(), reviewee.getNickname(), exhibition.getId());
    }
}
