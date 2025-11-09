package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.user.entity.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long>, JpaSpecificationExecutor<Artwork> {

    /**
     * 작가별 작품 수 조회
     * @param artist 작가 엔티티
     * @return 작품 수
     */
    int countByArtist(Artist artist);

    /**
     * 작가별 작품 목록 조회 (페이징)
     * @param artist 작가 엔티티
     * @param pageable 페이징 정보
     * @return 작품 목록 (페이징)
     */
    Page<Artwork> findByArtist(Artist artist, Pageable pageable);
}
