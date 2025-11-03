package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CafeImageRequest;
import com.ssafy.yeobaek.cafe.entity.Cafe;

import java.util.List;

public interface CafeImageService {

    /**
     * 대표 이미지 유효성 검사
     * @param images 이미지 요청 목록
     */
    void validatePrimaryImage(List<CafeImageRequest> images);

    /**
     * 카페 이미지 저장
     * @param cafe 카페 엔티티
     * @param images 이미지 요청 목록
     */
    void saveImages(Cafe cafe, List<CafeImageRequest> images);
}
