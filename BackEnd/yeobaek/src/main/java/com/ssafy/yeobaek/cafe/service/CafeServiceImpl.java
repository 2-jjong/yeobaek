package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.Coordinates;
import com.ssafy.yeobaek.cafe.dto.response.ExhibitionInfo;
import com.ssafy.yeobaek.cafe.dto.response.MyCafeData;
import com.ssafy.yeobaek.cafe.dto.response.Wall;
import com.ssafy.yeobaek.cafe.entity.*;
import com.ssafy.yeobaek.cafe.mapper.CafeMapper;
import com.ssafy.yeobaek.cafe.repository.CafeCategoryTagRepository;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.cafe.repository.DisplaySlotRepository;
import com.ssafy.yeobaek.cafe.repository.DisplayWallRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.ExhibitionStatus;
import com.ssafy.yeobaek.common.enums.WallStatus;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Cafe cafe = cafeMapper.toEntity(request, user, coordinates);
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
     * @return 등록된 공간 목록
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

        List<Wall> createdWalls = new ArrayList<>();

        // 3. 요청된 벽 정보들을 순회하며 저장
        request.forEach(wallRequest -> {
            DisplayWall displayWall = cafeMapper.toDisplayWallEntity(wallRequest, cafe);
            displayWallRepository.save(displayWall);

            // 4. 전시 가능한 벽인 경우, 슬롯 정보도 함께 저장
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
     * 나의 카페 조회
     * @param userId 유저 id
     * @return 등록된 공간 목록
     */
    @Transactional
    public MyCafeData getMyCafe(Long userId) {

        // 1. 카페 조회
        Cafe cafe = cafeRepository.findByCafeOwnerId(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        // 2. 벽 + 슬롯 조회
        List<DisplayWall> walls = displayWallRepository.findAllWithSlotByCafeId(cafe.getId());

        // 3. DTO 변환하여 반환
        return cafeMapper.toMyCafeData(cafe, walls);
    }
}
