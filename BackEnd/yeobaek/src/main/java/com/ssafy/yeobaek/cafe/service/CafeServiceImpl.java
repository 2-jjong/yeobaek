package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.artwork.dto.response.Pagination;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import com.ssafy.yeobaek.cafe.entity.CafeCategoryTag;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import com.ssafy.yeobaek.cafe.mapper.CafeMapper;
import com.ssafy.yeobaek.cafe.repository.*;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final UserService userService;
    private final CafeCategoryService cafeCategoryService;
    private final CafeCategoryTagRepository cafeCategoryTagRepository;
    private final GeocoderService geocoderService;
    private final CafeMapper cafeMapper;
    private final DisplayWallRepository displayWallRepository;
    private final DisplaySlotRepository displaySlotRepository;

    // Haversine 공식으로 두 지점 간의 거리를 계산 (km 단위)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void createCafe(Long userId, CreateCafeRequest request) {
        User user = userService.findById(userId)
            .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        if (cafeRepository.existsByCafeOwner(user)) {
            throw new CustomException(ResponseCode.CAFE_ALREADY_EXISTS);
        }
        Coordinates coordinates = geocoderService.getCoordinates(request.getAddress());
        Cafe cafe = cafeMapper.toEntity(request, user, coordinates);
        cafeRepository.save(cafe);
        List<CafeCategory> categories = cafeCategoryService.findCategoriesByIds(request.getCategoryIds());
        categories.forEach(cafeCategory -> {
            CafeCategoryTag cafeCategoryTag = CafeCategoryTag.builder()
                .cafe(cafe)
                .category(cafeCategory)
                .build();
            cafeCategoryTagRepository.save(cafeCategoryTag);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<Wall> createCafeWall(Long userId, List<CreateDisplayWallRequest> request) {
        User user = userService.findById(userId)
            .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        Cafe cafe = cafeRepository.findByCafeOwner(user)
            .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));
        if (displayWallRepository.existsByCafe(cafe)) {
            throw new CustomException(ResponseCode.WALL_ALREADY_EXISTS);
        }
        List<Wall> createdWalls = new ArrayList<>();
        request.forEach(wallRequest -> {
            DisplayWall displayWall = cafeMapper.toDisplayWallEntity(wallRequest, cafe);
            displayWallRepository.save(displayWall);
            if (wallRequest.getStatus()) {
                DisplaySlot displaySlot = DisplaySlot.builder()
                    .id(displayWall.getId())
                    .build();
                displaySlotRepository.save(displaySlot);
            }
            createdWalls.add(cafeMapper.toWallDto(displayWall));
        });
        return createdWalls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public MyCafeData getMyCafe(Long userId) {
        Cafe cafe = cafeRepository.findByCafeOwnerId(userId)
            .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));
        List<DisplayWall> walls = displayWallRepository.findAllWithSlotByCafeId(cafe.getId());
        return cafeMapper.toMyCafeData(cafe, walls);
    }

    /**
     * 카페 목록 조회 및 검색
     * @param keyword 검색어
     * @param latitude 위도
     * @param longitude 경도
     * @param minWidth 최소 너비
     * @param maxWidth 최대 너비
     * @param minHeight 최소 높이
     * @param maxHeight 최대 높이
     * @param minCommission 최소 수수료
     * @param maxCommission 최대 수수료
     * @param page 페이지
     * @param size 사이즈
     * @return 카페 목록 데이터
     */
    @Override
    @Transactional(readOnly = true)
    public CafeListData searchCafes(Boolean isArtist, String keyword, Double latitude, Double longitude, Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight, Double minCommission, Double maxCommission, Integer page, Integer size) {

        // 작가 전용 필터 권한 검사
        if (!isArtist) {
            if (minWidth != null || maxWidth != null || minHeight != null || maxHeight != null) {
                throw new com.ssafy.yeobaek.common.exception.CustomException(com.ssafy.yeobaek.common.code.ResponseCode.CAFE_SLOT_FILTER_FORBIDDEN);
            }
            if (minCommission != null || maxCommission != null) {
                throw new com.ssafy.yeobaek.common.exception.CustomException(com.ssafy.yeobaek.common.code.ResponseCode.CAFE_COMMISSION_FILTER_FORBIDDEN);
            }
        }

        // 1. 필터 조건 생성
        Specification<Cafe> spec = CafeSpecification.withFilters(keyword, minWidth, maxWidth, minHeight, maxHeight, minCommission, maxCommission);

        // 2. 필터링된 모든 카페를 조회 (정렬 x)
        List<Cafe> filteredCafes = cafeRepository.findAll(spec);

        // 3. Java 코드 내에서 거리 계산 및 DTO 변환, 정렬
        List<CafeSummary> summaries = filteredCafes.stream()
            .map(cafe -> {
                // 거리 계산
                double distance = calculateDistance(latitude, longitude, cafe.getLatitude(), cafe.getLongitude());

                // 슬롯 개수 계산
                int totalSlotCount = cafe.getDisplayWalls().size();
                int availableSlotCount = (int) cafe.getDisplayWalls().stream().filter(DisplayWall::getStatus).count();

                // 카테고리 ID 목록 추출
                List<Long> categoryIds = cafeCategoryTagRepository.findAllByCafe(cafe).stream()
                    .map(tag -> tag.getCategory().getId())
                    .collect(Collectors.toList());

                // 대표 이미지 URL 추출
                String primaryImageUrl = cafe.getImages().isEmpty() ? null : cafe.getImages().get(0);

                return new CafeSummary(cafe.getId(), cafe.getCafeName(), cafe.getAddress(), primaryImageUrl, availableSlotCount, totalSlotCount, distance, categoryIds);
            })
            .sorted(Comparator.comparing(CafeSummary::getDistance)) // 거리순으로 정렬
            .collect(Collectors.toList());

        // 4. 페이지네이션 처리
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), summaries.size());
        List<CafeSummary> pagedContent = (start > end) ? Collections.emptyList() : summaries.subList(start, end);

        Page<CafeSummary> cafeSummaryPage = new PageImpl<>(pagedContent, pageable, summaries.size());

        // 5. 최종 결과물 조합
        Pagination pagination = new Pagination(cafeSummaryPage.getNumber(), cafeSummaryPage.getSize(), cafeSummaryPage.getTotalElements(), cafeSummaryPage.getTotalPages(), cafeSummaryPage.isLast());

        return new CafeListData(cafeSummaryPage.getContent(), pagination);
    }
}
