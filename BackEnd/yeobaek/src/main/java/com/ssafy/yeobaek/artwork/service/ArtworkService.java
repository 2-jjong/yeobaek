package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.ArtworkDetailData;
import com.ssafy.yeobaek.artwork.dto.response.ArtworkListData;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.enums.ArtworkSortBy;

import java.util.List;

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

    /**
     * 작품 검색 (키워드, 필터, 정렬)
     * 키워드가 있으면 작가/작품 통합 검색을 수행하고, 없으면 필터링과 정렬만 적용합니다.
     * 규격 필터(가로/세로)는 카페 사장만 사용할 수 있습니다.
     * @param userId 현재 사용자 ID (권한 검증용, null 가능)
     * @param keyword 작품명/작가명 검색 키워드 (null 가능)
     * @param categories 카테고리 필터 목록 (null 가능)
     * @param minWidth 최소 가로 크기 (cm 단위, null 가능)
     * @param maxWidth 최대 가로 크기 (cm 단위, null 가능)
     * @param minHeight 최소 세로 크기 (cm 단위, null 가능)
     * @param maxHeight 최대 세로 크기 (cm 단위, null 가능)
     * @param sortBy 정렬 기준 (LATEST: 최신순, POPULARITY: 인기순)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 작품 목록 및 작가 목록 (키워드가 있으면 작가 목록 포함, 없으면 null)
     */
    ArtworkListData searchArtworks(
            Long userId,
            String keyword,
            List<ArtCategory> categories,
            Integer minWidth,
            Integer maxWidth,
            Integer minHeight,
            Integer maxHeight,
            ArtworkSortBy sortBy,
            Integer page,
            Integer size
    );
}
