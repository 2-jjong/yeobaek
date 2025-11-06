package com.ssafy.yeobaek.artwork.service;

import com.ssafy.yeobaek.artwork.dto.request.CreateArtworkRequest;
import com.ssafy.yeobaek.artwork.dto.response.*;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.mapper.ArtworkMapper;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.ArtCategory;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final ArtworkBookmarkService artworkBookmarkService;
    private final UserService userService;
    private final ArtworkMapper artworkMapper;

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

    /**
     * 작품 상세 조회
     * @param artworkId 작품 ID
     * @param userId 현재 사용자 ID (북마크 여부 확인용)
     * @return 작품 상세 정보
     */
    @Override
    @Transactional
    public ArtworkDetailData getArtworkDetail(Long artworkId, Long userId) {
        // 작품 조회 (없으면 예외 발생)
        Artwork artwork = artworkRepository.findById(artworkId)
                .orElseThrow(() -> new CustomException(ResponseCode.ARTWORK_NOT_FOUND));

        // 조회수 증가
        artwork.incrementViewCount();

        // 북마크 여부 확인
        boolean isBookmarked = artworkBookmarkService.isBookmarked(userId, artworkId);

        // DTO로 변환하여 반환
        return artworkMapper.toArtworkDetailData(artwork, isBookmarked);
    }

    /**
     * 키워드로 작품/작가 검색
     * @param keyword 검색 키워드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 작품 및 작가 목록
     */
    @Override
    @Transactional(readOnly = true)
    public ArtworkListData searchByKeyword(String keyword, Integer page, Integer size) {
        // 작품명으로 검색
        List<Artwork> artworks = artworkRepository.findByTitleContaining(keyword);

        // 작가명으로 검색
        List<Artist> artists = userService.searchArtistsByKeyword(keyword);

        // Artist를 ArtistSummary로 변환
        List<ArtistSummary> artistSummaries = artists.stream()
                .map(artist -> {
                    int artworkCount = artworkRepository.countByArtist(artist);
                    return artworkMapper.toArtistSummary(artist, artworkCount);
                })
                .collect(Collectors.toList());

        // 작품을 ArtworkSummary로 변환
        List<ArtworkSummary> artworkSummaries = artworks.stream()
                .map(artworkMapper::toArtworkSummary)
                .collect(Collectors.toList());

        // TODO: 페이징 처리 구현 필요
        long totalElements = artworkSummaries.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages - 1;

        Pagination pagination = new Pagination(page, size, totalElements, totalPages, hasNext);

        // 결과 반환
        return new ArtworkListData(artistSummaries, artworkSummaries, pagination);
    }
}
