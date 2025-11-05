package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.ArtworkBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkBookmarkRepository extends JpaRepository<ArtworkBookmark, Long> {

    /**
     * 특정 사용자가 특정 작품을 북마크했는지 확인
     * @param userId 사용자 ID
     * @param artworkId 작품 ID
     * @return 북마크 여부
     */
    boolean existsByUser_IdAndArtwork_Id(Long userId, Long artworkId);
}
