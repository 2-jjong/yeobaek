package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CafeImageRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import com.ssafy.yeobaek.cafe.entity.CafeImage;
import com.ssafy.yeobaek.cafe.repository.CafeCategoryRepository;
import com.ssafy.yeobaek.cafe.repository.CafeImageRepository;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.common.util.SecurityUtil;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final UserRepository userRepository;
    private final CafeCategoryRepository cafeCategoryRepository;
    private final CafeImageRepository cafeImageRepository;

    /**
     * 카페 등록
     * @param request 카페 등록 정보
     */
    @Override
    @Transactional
    public void createCafe(CreateCafeRequest request) {

        // 1. 유효성 검사
        validateCreateRequest(request);

        // 2. 사용자 정보 확인
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 3. 권한 확인 (카페 사장)
        if (SecurityUtil.getCurrentUserType() != UserRole.CAFE_OWNER) {
            throw new CustomException(ResponseCode.CAFE_FORBIDDEN_CREATE);
        }

        // 4. 중복 등록 확인
        if (cafeRepository.existsByCafeOwner(user)) {
            throw new CustomException(ResponseCode.CAFE_ALREADY_EXISTS);
        }

        // 5. 카페 엔티티 생성 및 저장
        Cafe cafe = Cafe.builder()
                .cafeOwner(user)
                .cafeName(request.getCafeName())
                .address(request.getAddress())
                .description(request.getDescription())
                .contact(request.getContact())
                .businessStartTime(request.getBusinessHoursStart())
                .businessEndTime(request.getBusinessHoursEnd())
                .dailyVisitors(request.getDailyVisitors())
                .viewCount(0)
                .bookmarkCount(0)
                .build();
        cafeRepository.save(cafe);

        // 6. 카페 카테고리 저장
        request.getCategories().forEach(categoryName -> {
            CafeCategory cafeCategory = CafeCategory.builder()
                    .cafe(cafe)
                    .categoryName(categoryName)
                    .build();
            cafeCategoryRepository.save(cafeCategory);
        });

        // 7. 카페 이미지 저장
        request.getImages().forEach(imageRequest -> {
            CafeImage cafeImage = CafeImage.builder()
                    .cafe(cafe)
                    .imageUrl(imageRequest.getImageUrl())
                    .isPrimary(imageRequest.getIsPrimary())
                    .displayOrder(imageRequest.getDisplayOrder())
                    .build();
            cafeImageRepository.save(cafeImage);
        });
    }

    private void validateCreateRequest(CreateCafeRequest request) {
        // 매장명 길이 (1~15자)
        if (request.getCafeName() == null || request.getCafeName().isBlank() || request.getCafeName().isEmpty() || request.getCafeName().length() > 15) {
            throw new CustomException(ResponseCode.CAFE_INVALID_NAME_LENGTH);
        }

        // 카페 소개 길이 (최대 500자)
        if (request.getDescription() != null && request.getDescription().length() > 500) {
            throw new CustomException(ResponseCode.CAFE_INVALID_DESCRIPTION_LENGTH);
        }

        List<CafeImageRequest> images = request.getImages();
        // 이미지 개수 (3~10장)
        if (images == null || images.size() < 3) {
            throw new CustomException(ResponseCode.CAFE_MIN_IMAGES);
        }
        if (images.size() > 10) {
            throw new CustomException(ResponseCode.CAFE_MAX_IMAGES);
        }

        // 대표 이미지 1개
        long primaryImageCount = images.stream().filter(CafeImageRequest::getIsPrimary).count();
        if (primaryImageCount != 1) {
            throw new CustomException(ResponseCode.CAFE_PRIMARY_IMAGE_ONLY_ONE);
        }

        // 영업시간 형식 (HH:mm)
        String timeRegex = "^([01]\\d|2[0-3]):([0-5]\\d)$";
        if (!request.getBusinessHoursStart().matches(timeRegex) || !request.getBusinessHoursEnd().matches(timeRegex)) {
            throw new CustomException(ResponseCode.CAFE_INVALID_HOURS_FORMAT);
        }
    }
}
