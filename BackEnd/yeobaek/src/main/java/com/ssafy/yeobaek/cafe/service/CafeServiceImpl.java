package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CafeImageRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import com.ssafy.yeobaek.cafe.entity.CafeCategoryTag;
import com.ssafy.yeobaek.cafe.entity.CafeImage;
import com.ssafy.yeobaek.cafe.repository.CafeCategoryRepository;
import com.ssafy.yeobaek.cafe.repository.CafeCategoryTagRepository;
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
    private final CafeCategoryTagRepository cafeCategoryTagRepository;
    private final CafeImageRepository cafeImageRepository;

    /**
     * 카페 등록
     * @param request 카페 등록 정보
     */
    @Override
    @Transactional
    public void createCafe(CreateCafeRequest request) {

        // 1. 대표 이미지 1개 확인
        long primaryImageCount = request.getImages().stream().filter(CafeImageRequest::getIsPrimary).count();
        if (primaryImageCount != 1) {
            throw new CustomException(ResponseCode.CAFE_PRIMARY_IMAGE_ONLY_ONE);
        }

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
        request.getCategoryIds().forEach(categoryId -> {
            CafeCategory cafeCategory = cafeCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CustomException(ResponseCode.CAFE_CATEGORY_NOT_FOUND));

            CafeCategoryTag cafeCategoryTag = CafeCategoryTag.builder()
                    .cafe(cafe)
                    .category(cafeCategory)
                    .build();
            cafeCategoryTagRepository.save(cafeCategoryTag);
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

}
