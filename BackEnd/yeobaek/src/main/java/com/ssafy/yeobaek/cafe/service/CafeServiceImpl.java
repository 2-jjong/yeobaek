package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.cafe.dto.request.UpdateSlotRequest;
import com.ssafy.yeobaek.common.dto.Pagination;
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
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.service.ExhibitionService;
import com.ssafy.yeobaek.storage.service.S3Service;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final CafeCategoryTagRepository cafeCategoryTagRepository;
    private final CafeBookmarkRepository cafeBookmarkRepository;
    private final DisplayWallRepository displayWallRepository;
    private final DisplaySlotRepository displaySlotRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final GeocoderService geocoderService;
    private final CafeCategoryService cafeCategoryService;
    private final CafeBookmarkService cafeBookmarkService;
    private final ExhibitionService exhibitionService;
    private final CafeMapper cafeMapper;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * 카페 등록
     * @param userId 유저 id
     * @param request 카페 등록 정보
     */
    @Override
    @Transactional
    public void createCafe(Long userId, CreateCafeRequest request) {

        // 1. 사용자 정보 확인
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 2. 중복 등록 확인
        if (cafeRepository.existsByCafeOwner(user)) {
            throw new CustomException(ResponseCode.CAFE_ALREADY_EXISTS);
        }

        // 3. 주소를 좌표로 변환
        Coordinates coordinates = geocoderService.getCoordinates(request.getAddress());

        // 4. 카페 엔티티 생성 및 저장
        Cafe cafe = cafeMapper.toCafeEntity(request, user, coordinates);
        cafeRepository.save(cafe);

        // 5. 카페 카테고리 저장
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
     * 카페 공간 등록
     * @param userId 유저 id
     * @param request 카페 공간 등록 정보
     */
    @Override
    @Transactional
    public List<Wall> createCafeWall(Long userId, List<CreateDisplayWallRequest> request) {

        // 1. 사용자 정보 확인 및 카페 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));
        Cafe cafe = cafeRepository.findByCafeOwner(user)
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        // 2. 중복 등록 확인
        if (displayWallRepository.existsByCafe(cafe)) {
            throw new CustomException(ResponseCode.WALL_ALREADY_EXISTS);
        }

        // 3. 요청된 벽 정보들을 순회하며 저장
        List<Wall> createdWalls = new ArrayList<>();
        request.forEach(wallRequest -> {
            DisplayWall displayWall = cafeMapper.toDisplayWallEntity(wallRequest, cafe);
            displayWallRepository.save(displayWall);

            // 4. 전시 가능한 벽인 경우, 슬롯 정보도 함께 저장
            if (wallRequest.getStatus()) {
                DisplaySlot displaySlot = DisplaySlot.builder()
                        .wall(displayWall)
                        .build();
                displaySlotRepository.save(displaySlot);
            }
            createdWalls.add(cafeMapper.toWallDto(displayWall));
        });
        return createdWalls;
    }

    /**
     * 나의 카페 조회
     * @param userId 유저 id
     * @return 나의 카페 정보 및 공간 정보
     */
    @Override
    public MyCafeData getMyCafe(Long userId) {

        // 1. 카페 조회
        Cafe cafe = cafeRepository.findByCafeOwnerId(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        // 2. 벽 + 슬롯 조회
        List<DisplayWall> walls = displayWallRepository.findAllWithSlotByCafeId(cafe.getId());

        // 3. 전시 정보 조회 및 맵 생성
        Map<Long, ExhibitionInfo> exhibitionInfoMap = walls.stream()
                .map(DisplayWall::getSlot)
                .filter(Objects::nonNull)
                .map(exhibitionService::findOngoingExhibitionBySlot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        exhibition -> exhibition.getDisplaySlot().getId(),
                        this::createExhibitionInfo
                ));

        // 4. 카페 이미지 URL 변환
        String primaryImage = s3Service.generatePresignedUrlForDownload(cafe.getImages().get(0));

        // 5. DTO 변환하여 반환
        return cafeMapper.toMyCafeData(cafe, walls, primaryImage, exhibitionInfoMap);
    }


    /**
     * 카페 목록 조회 및 검색
     * @param isArtist 작가 여부
     * @param keyword 검색어
     * @param myLatitude 현재 위치 위도
     * @param myLongitude 현재 위치 경도
     * @param centerLatitude 지도 중심 위도
     * @param centerLongitude 지도 중심 경도
     * @param radiusKm 조회 반경
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
    public CafeListData searchCafes(Boolean isArtist, String keyword, Double myLatitude, Double myLongitude, Double centerLatitude, Double centerLongitude, Double radiusKm, Integer minWidth, Integer maxWidth, Integer minHeight, Integer maxHeight, Double minCommission, Double maxCommission, Integer page, Integer size) {

        // 작가 전용 필터 권한 검사
        if (!isArtist) {
            if (minWidth != null || maxWidth != null || minHeight != null || maxHeight != null) {
                throw new CustomException(ResponseCode.SLOT_FILTER_FORBIDDEN);
            }
            if (minCommission != null || maxCommission != null) {
                throw new CustomException(ResponseCode.CAFE_COMMISSION_FILTER_FORBIDDEN);
            }
        }

        // 1. 필터 조건 생성
        Specification<Cafe> spec = CafeSpecification.withFilters(keyword, centerLatitude, centerLongitude, radiusKm, minWidth, maxWidth, minHeight, maxHeight, minCommission, maxCommission);

        // 2. 페이지네이션 정보 생성
        Pageable pageable = PageRequest.of(page, size);

        // 3. 필터링 및 페이지네이션된 카페 조회
        Page<Cafe> cafePage = cafeRepository.findAll(spec, pageable);

        // 4. 조회된 카페들의 카테고리 태그를 한 번에 조회하여 맵으로 구성 (N+1 문제 방지)
        List<Long> cafeIds = cafePage.getContent().stream()
                .map(Cafe::getId)
                .collect(Collectors.toList());
        Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())));

        // 5. DTO 변환
        List<CafeSummary> summaries = cafePage.getContent().stream()
                .map(cafe -> {
                    // 거리 계산 (사용자 위치 기준)
                    double distance = calculateDistance(myLatitude, myLongitude, cafe.getLatitude(), cafe.getLongitude());

                    // 카테고리 목록 추출
                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), Collections.emptyList());

                    // 대표 이미지 URL 추출
                    String primaryImageUrl = s3Service.generatePresignedUrlForDownload(cafe.getImages().get(0));

                    return cafeMapper.toCafeSummary(cafe, distance, categories, primaryImageUrl);
                })
                .collect(Collectors.toList());

        // 6. 페이지네이션 DTO 생성
        Pagination pagination = cafeMapper.toPagination(cafePage);

        return new CafeListData(summaries, pagination);
    }

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
     * 카페 상세 정보 조회
     * @param userId 유저 id
     * @param cafeId 카페 id
     * @return 카페 상세 정보
     */
    @Override
    @Transactional
    public CafeDetailData getCafeDetail(Long userId, Long cafeId) {

        // 1. Cafe 엔티티 조회
        Cafe cafe = cafeRepository.findById(cafeId)
            .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        // 2. 벽, 슬롯, 전시 정보 조회
        List<DisplayWall> walls = displayWallRepository.findAllWithSlotByCafeId(cafeId);

        // 3. 전시 정보 조회 및 맵 생성
        Map<Long, ExhibitionInfo> exhibitionInfoMap = walls.stream()
                .map(DisplayWall::getSlot)
                .filter(Objects::nonNull)
                .map(exhibitionService::findCurrentOrUpcomingExhibitionBySlot)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        exhibition -> exhibition.getDisplaySlot().getId(),
                        this::createExhibitionInfo
                ));

        // 4. 카테고리 정보 조회
        List<String> categories = cafeCategoryTagRepository.findAllByCafe(cafe).stream()
            .map(tag -> tag.getCategory().getCategoryName())
            .collect(Collectors.toList());

        // 5. 카페 이미지 URL 변환
        List<String> images = s3Service.generatePresignedUrlsForDownload(cafe.getImages());
        String profileImage = s3Service.generatePresignedUrlForDownload(cafe.getCafeOwner().getProfileImageUrl());

        // 6. 북마크 여부 확인
        boolean isBookmarked = cafeBookmarkService.isBookmarked(userId, cafeId);

        // 7. DTO 변환
        CafeDetailData cafeDetailData = cafeMapper.toCafeDetailData(cafe, walls, categories, images, profileImage, exhibitionInfoMap, isBookmarked);

        // 8. 조회수 1 증가
        cafe.increaseViewCount();
        cafeRepository.save(cafe);

        return cafeDetailData;
    }

    /**
     * 슬롯 정보 수정
     * @param userId 유저 id
     * @param cafeId 카페 id
     * @param slotId 슬롯 id
     * @param request 슬롯 수정 정보
     * @return 슬롯 정보
     */
    @Override
    @Transactional
    public SlotData updateSlot(Long userId, Long cafeId, Long slotId, UpdateSlotRequest request) {

        // 1. 슬롯 조회
        DisplaySlot slot = displaySlotRepository.findById(slotId)
                .orElseThrow(() -> new CustomException(ResponseCode.SLOT_NOT_FOUND));

        // 2. 슬롯이 속한 카페 확인
        Cafe cafe = slot.getWall().getCafe();
        if (!Objects.equals(cafeId, cafe.getId())) {
            throw new CustomException(ResponseCode.SLOT_NOT_FOUND_IN_CAFE);
        }

        // 3. 슬롯 소유 카페 확인 및 권한 검사
        if (!cafe.getCafeOwner().getId().equals(userId)) {
            throw new CustomException(ResponseCode.CAFE_FORBIDDEN_UPDATE_DELETE);
        }

        // 4. 슬롯 정보 업데이트
        if (request.getCommissionRate() != null) {
            slot.setCommissionRate(request.getCommissionRate());
        }
        if (request.getRequirements() != null) {
            slot.setRequirements(request.getRequirements());
        }

        // 5. 수정된 정보 저장
        displaySlotRepository.save(slot);

        // 6. DTO 반환
        return cafeMapper.toSlotData(slot);
    }

    private ExhibitionInfo createExhibitionInfo(Exhibition exhibition) {
        Artwork artwork = exhibition.getArtwork();
        String artworkImageUrl = artwork.getImages().isEmpty() ? null : s3Service.generatePresignedUrlForDownload(artwork.getImages().get(0));

        return new ExhibitionInfo(
                artwork.getId(),
                artwork.getTitle(),
                artworkImageUrl,
                artwork.getArtist().getUser().getNickname(),
                exhibition.getStartDate().format(DATE_FORMATTER),
                exhibition.getEndDate().format(DATE_FORMATTER)
        );
    }

    /**
     * 북마크한 카페 목록 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 북마크한 카페 목록 데이터
     */
    @Override
    public CafeListData getBookmarkedCafes(Long userId, Integer page, Integer size) {

        // 1. Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);

        // 2. 사용자가 북마크한 카페 목록을 페이지네이션하여 조회
        Page<Cafe> cafePage = cafeBookmarkRepository.findBookmarkedCafesByUserId(userId, pageable);

        // 3. 조회된 카페들의 카테고리 태그를 한 번에 조회하여 맵으로 구성 (N+1 문제 방지)
        List<Long> cafeIds = cafePage.getContent().stream()
                .map(Cafe::getId)
                .collect(Collectors.toList());
        Map<Long, List<String>> cafeCategoriesMap = cafeCategoryTagRepository.findAllByCafe_IdIn(cafeIds).stream()
                .collect(Collectors.groupingBy(tag -> tag.getCafe().getId(),
                        Collectors.mapping(tag -> tag.getCategory().getCategoryName(), Collectors.toList())));

        // 4. DTO 변환
        List<CafeSummary> summaries = cafePage.getContent().stream()
                .map(cafe -> {
                    // 카페 카테고리
                    List<String> categories = cafeCategoriesMap.getOrDefault(cafe.getId(), Collections.emptyList());

                    // 카페 대표 이미지 URL 변환
                    String primaryImageUrl = s3Service.generatePresignedUrlForDownload(cafe.getImages().get(0));

                    // 북마크 목록에서는 거리를 계산하지 않으므로 null 전달
                    return cafeMapper.toCafeSummary(cafe, null, categories, primaryImageUrl);
                })
                .collect(Collectors.toList());

        // 5. 페이지네이션 DTO 생성
        Pagination pagination = cafeMapper.toPagination(cafePage);

        return new CafeListData(summaries, pagination);
    }
}
