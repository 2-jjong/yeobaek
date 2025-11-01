package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;

public interface CafeService {

    /**
     * 카페 등록
     * @param request 카페 등록 정보
     */
    void createCafe(CreateCafeRequest request);
}
