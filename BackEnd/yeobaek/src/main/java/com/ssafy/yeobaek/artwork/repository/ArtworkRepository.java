package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.user.entity.Artist;
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
}
