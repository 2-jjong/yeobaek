package com.ssafy.yeobaek.exhibition.service;

import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;

public interface ExhibitionService {

    /**
     * 전시 제안 생성
     * @param userId 현재 사용자 ID (제안자)
     * @param request 전시 제안 요청 정보
     */
    void createExhibitionProposal(Long userId, CreateExhibitionRequest request);
}
