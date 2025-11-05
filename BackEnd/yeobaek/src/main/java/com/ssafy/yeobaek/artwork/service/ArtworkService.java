package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;

public interface ArtworkService {

    /**
     * 작품 등록
     * @param userId 사용자 ID
     * @param request 작품 등록 정보
     */
    void createArtwork(Long userId, CreateArtworkRequest request);
}
