package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;

import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.MyCafeData;
import com.ssafy.yeobaek.cafe.dto.response.CafeListData;
import com.ssafy.yeobaek.cafe.dto.response.Wall;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CafeService {

    /**
     * 카페 등록
     * @param userId 유저 id
     * @param request 카페 등록 정보
     */
    void createCafe(Long userId, CreateCafeRequest request);

    /**
     * 카페 공간 등록
     * @param userId 유저 id
     * @param request 카페 공간 등록 정보
     */
    List<Wall> createCafeWall(Long userId, List<CreateDisplayWallRequest> request);

    /**
     * 나의 카페 조회
     * @param userId 유저 id
     * @return 나의 카페 정보 및 공간 정보
     */
    MyCafeData getMyCafe(Long userId);

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
    CafeListData searchCafes(Boolean isArtist, String keyword, Double latitude, Double longitude, Integer minWidth, Integer maxWidth,
        Integer minHeight, Integer maxHeight, Double minCommission, Double maxCommission, Integer page, Integer size);
}
