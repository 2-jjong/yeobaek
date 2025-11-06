package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;

import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.MyCafeData;
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

    @Transactional
    MyCafeData getMyCafe(Long userId);
}
