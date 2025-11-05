package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.repository.ArtworkBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtworkBookmarkServiceImpl implements ArtworkBookmarkService {

    private final ArtworkBookmarkRepository artworkBookmarkRepository;

    /**
     * 사용자가 특정 작품을 북마크했는지 확인
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return 북마크 여부
     */
    @Override
    public boolean isBookmarked(Long userId, Long artworkId) {
        return artworkBookmarkRepository.existsByUser_IdAndArtwork_Id(userId, artworkId);
    }
}
