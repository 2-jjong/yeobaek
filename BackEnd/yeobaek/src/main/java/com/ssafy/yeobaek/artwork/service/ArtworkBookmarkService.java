package com.ssafy.yeobaek.artwork.service;

/**
 * 작품 북마크 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface ArtworkBookmarkService {

    /**
     * 사용자가 특정 작품을 북마크했는지 확인
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return 북마크 여부
     */
    boolean isBookmarked(Long userId, Long artworkId);
}
