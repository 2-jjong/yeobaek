package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.ArtworkDetailData;

public interface ArtworkService {

    /**
     * 작품 등록
     * @param userId 사용자 ID
     * @param request 작품 등록 정보
     */
    void createArtwork(Long userId, CreateArtworkRequest request);

    /**
     * 작품 상세 조회
     * @param artworkId 작품 ID
     * @param userId 현재 사용자 ID (북마크 여부 확인용)
     * @return 작품 상세 정보
     */
    ArtworkDetailData getArtworkDetail(Long artworkId, Long userId);
}
