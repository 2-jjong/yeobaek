package com.ssafy.yeobaek.artwork.repository;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

}
