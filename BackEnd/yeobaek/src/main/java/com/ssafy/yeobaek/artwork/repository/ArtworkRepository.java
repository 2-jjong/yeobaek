package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.user.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    /**
     * 작품명으로 검색
     * @param keyword 검색 키워드
     * @return 작품명에 키워드가 포함된 작품 목록
     */
    List<Artwork> findByTitleContaining(String keyword);

    /**
     * 작가별 작품 수 조회
     * @param artist 작가 엔티티
     * @return 작품 수
     */
    int countByArtist(Artist artist);
}
