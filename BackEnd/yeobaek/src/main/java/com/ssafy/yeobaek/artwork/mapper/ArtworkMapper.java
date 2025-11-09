package com.ssafy.yeobaek.artwork.mapper;

import com.ssafy.yeobaek.artwork.dto.response.*;
import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Artwork 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class ArtworkMapper {

    /**
     * Artwork 엔티티를 ArtworkDetail DTO로 변환
     * @param artwork Artwork 엔티티
     * @param isBookmarked 현재 사용자의 북마크 여부
     * @param artworkImageUrls 변환된 작품 이미지 URL 리스트 (presigned URL)
     * @param artistProfileImageUrl 변환된 작가 프로필 이미지 URL (presigned URL)
     * @return ArtworkDetail DTO
     */
    public ArtworkDetail toArtworkDetail(Artwork artwork, boolean isBookmarked,
                                         List<String> artworkImageUrls, String artistProfileImageUrl) {
        Artist artist = toArtist(artwork.getArtist(), artistProfileImageUrl);

        // 전시 중일 때만 Exhibition 정보 포함 (현재는 더미 데이터)
        Exhibition exhibition = null;
        if (artwork.getExhibitionStatus().name().equals("EXHIBITING")) {
            exhibition = toDummyExhibition();
        }

        return ArtworkDetail.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .price(artwork.getPrice())
                .category(artwork.getArtCategory())
                .widthCm(artwork.getWidthCm().intValue())
                .heightCm(artwork.getHeightCm().intValue())
                .weightKg(artwork.getWeightKg())
                .productionYear(artwork.getProductionYear())
                .status(artwork.getExhibitionStatus())
                .viewCount(artwork.getViewCount())
                .bookmarkCount(artwork.getBookmarkCount())
                .isBookmarked(isBookmarked)
                .images(artworkImageUrls)
                .artist(artist)
                .currentExhibition(exhibition)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkDetailData로 변환
     * @param artwork Artwork 엔티티
     * @param isBookmarked 현재 사용자의 북마크 여부
     * @param artworkImageUrls 변환된 작품 이미지 URL 리스트 (presigned URL)
     * @param artistProfileImageUrl 변환된 작가 프로필 이미지 URL (presigned URL)
     * @return ArtworkDetailData
     */
    public ArtworkDetailData toArtworkDetailData(Artwork artwork, boolean isBookmarked,
                                                 List<String> artworkImageUrls, String artistProfileImageUrl) {
        ArtworkDetail artworkDetail = toArtworkDetail(artwork, isBookmarked, artworkImageUrls, artistProfileImageUrl);
        return ArtworkDetailData.builder()
                .artwork(artworkDetail)
                .build();
    }

    /**
     * Artist 엔티티를 Artist DTO로 변환
     * @param artist Artist 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @return Artist DTO
     */
    public Artist toArtist(com.ssafy.yeobaek.user.entity.Artist artist, String profileImageUrl) {
        return Artist.builder()
                .userId(artist.getUser().getId())
                .artistName(artist.getUser().getNickname())
                .profileImageUrl(profileImageUrl)
                .build();
    }

    /**
     * 더미 Exhibition 생성
     * TODO: Exhibition 엔티티 및 Repository 구현 후 toExhibition(Exhibition exhibition) 메서드로 변경
     * @return 더미 Exhibition
     */
    public Exhibition toDummyExhibition() {
        return Exhibition.builder()
                .cafeId(1L)
                .cafeName("카페 여백")
                .cafeAddress("서울시 강남구 테헤란로 123")
                .cafeImageUrl("https://example.com/cafe.jpg")
                .slotName("메인 전시 공간")
                .exhibitionStartDate(java.time.LocalDate.of(2025, 1, 1))
                .exhibitionEndDate(java.time.LocalDate.of(2025, 12, 31))
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummary DTO로 변환 (기본 정보만)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @return ArtworkSummary DTO
     */
    public ArtworkSummary toArtworkSummary(Artwork artwork, String thumbnailUrl) {
        return ArtworkSummary.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummary DTO로 변환 (확장된 정보 포함)
     * @param artwork Artwork 엔티티
     * @param thumbnailUrl 썸네일 이미지 URL (presigned URL)
     * @return ArtworkSummary DTO (모든 필드 포함)
     */
    public ArtworkSummary toArtworkSummaryWithDetails(Artwork artwork, String thumbnailUrl) {
        return ArtworkSummary.builder()
                .artworkId(artwork.getId())
                .title(artwork.getTitle())
                .thumbnailUrl(thumbnailUrl)
                .price(artwork.getPrice())
                .category(artwork.getArtCategory().name())
                .widthCm(artwork.getWidthCm().intValue())
                .heightCm(artwork.getHeightCm().intValue())
                .exhibitionStatus(artwork.getExhibitionStatus().name())
                .bookmarkCount(artwork.getBookmarkCount())
                .build();
    }

    /**
     * Artwork 엔티티를 ArtworkSummaryDetail DTO로 변환 (상세 버전)
     * @param artwork Artwork 엔티티
     * @return ArtworkSummaryDetail DTO
     */
    public ArtworkSummaryDetail toArtworkSummaryDetail(Artwork artwork) {
        return new ArtworkSummaryDetail(
                artwork.getId(),
                artwork.getTitle(),
                artwork.getImages().isEmpty() ? null : artwork.getImages().get(0),
                artwork.getArtist().getUser().getNickname(),
                artwork.getArtist().getUser().getProfileImageUrl(),
                artwork.getPrice(),
                artwork.getArtCategory(),
                artwork.getWidthCm().intValue(),
                artwork.getHeightCm().intValue(),
                artwork.getExhibitionStatus(),
                artwork.getBookmarkCount(),
                null  // TODO: cafeName은 추후 Exhibition 연동 시 추가
        );
    }

    /**
     * Artist 엔티티를 ArtistSummary DTO로 변환
     * @param artist Artist 엔티티
     * @param artworkCount 작품 수
     * @return ArtistSummary DTO
     */
    public ArtistSummary toArtistSummary(com.ssafy.yeobaek.user.entity.Artist artist, int artworkCount) {
        User user = artist.getUser();
        return new ArtistSummary(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                artist.getIntroduction(),
                artworkCount
        );
    }

    // TODO: Exhibition 엔티티 구현 후 아래 메서드 추가
    // /**
    //  * Exhibition 엔티티를 Exhibition DTO로 변환
    //  * @param exhibition Exhibition 엔티티
    //  * @return Exhibition DTO
    //  */
    // public Exhibition toExhibition(com.ssafy.yeobaek.exhibition.entity.Exhibition exhibition) {
    //     return Exhibition.builder()
    //             .cafeId(exhibition.getCafe().getCafeId())
    //             .cafeName(exhibition.getCafe().getCafeName())
    //             .cafeAddress(exhibition.getCafe().getAddress())
    //             .cafeImageUrl(exhibition.getCafe().getMainImageUrl())
    //             .slotName(exhibition.getSlot().getSlotName())
    //             .exhibitionStartDate(exhibition.getStartDate())
    //             .exhibitionEndDate(exhibition.getEndDate())
    //             .build();
    // }
}
