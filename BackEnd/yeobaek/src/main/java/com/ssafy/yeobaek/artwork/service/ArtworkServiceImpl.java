package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final UserService userService;

    /**
     * 작품 등록
     * @param userId 사용자 ID
     * @param request 작품 등록 정보
     */
    @Override
    @Transactional
    public void createArtwork(Long userId, CreateArtworkRequest request) {
        // Artist 프록시 객체 참조
        Artist artist = userService.getArtistReference(userId);

        // 카테고리 변환
        ArtCategory artCategory = ArtCategory.valueOf(request.getCategory());

        // Artwork 엔티티 생성 및 저장
        Artwork artwork = Artwork.builder()
                .artist(artist)
                .title(request.getTitle())
                .description(request.getDescription())
                .artCategory(artCategory)
                .price(request.getPrice())
                .widthCm(request.getWidthCm().doubleValue())
                .heightCm(request.getHeightCm().doubleValue())
                .weightKg(request.getWeightKg())
                .productionYear(request.getProductionYear())
                .images(request.getImages())
                .build();

        artworkRepository.save(artwork);
    }
}
