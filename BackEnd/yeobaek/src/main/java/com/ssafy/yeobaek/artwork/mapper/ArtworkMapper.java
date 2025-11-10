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
     * @param exhibition 현재 전시 정보 (없으면 null)
     * @return ArtworkDetail DTO
     */
    public ArtworkDetail toArtworkDetail(Artwork artwork, boolean isBookmarked, List<String> artworkImageUrls, String artistProfileImageUrl, Exhibition exhibition) {
        Artist artist = toArtist(artwork.getArtist(), artistProfileImageUrl);

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
                .isExhibiting(artwork.getIsExhibiting())
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
     * @param exhibition 현재 전시 정보 (없으면 null)
     * @return ArtworkDetailData
     */
    public ArtworkDetailData toArtworkDetailData(Artwork artwork, boolean isBookmarked, List<String> artworkImageUrls, String artistProfileImageUrl, Exhibition exhibition) {
        ArtworkDetail artworkDetail = toArtworkDetail(artwork, isBookmarked, artworkImageUrls, artistProfileImageUrl, exhibition);
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
     * Exhibition 엔티티를 Exhibition DTO로 변환
     * @param exhibition Exhibition 엔티티
     * @param cafeImageUrl 카페 이미지 URL (presigned URL)
     * @return Exhibition DTO
     */
    public Exhibition toExhibition(com.ssafy.yeobaek.exhibition.entity.Exhibition exhibition, String cafeImageUrl) {
        return Exhibition.builder()
                .cafeId(exhibition.getCafe().getId())
                .cafeName(exhibition.getCafe().getCafeName())
                .cafeAddress(exhibition.getCafe().getAddress())
                .cafeImageUrl(cafeImageUrl)
                .exhibitionStartDate(exhibition.getStartDate())
                .exhibitionEndDate(exhibition.getEndDate())
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
                .isExhibiting(artwork.getIsExhibiting())
                .bookmarkCount(artwork.getBookmarkCount())
                .build();
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

    /**
     * ArtworkListData DTO 생성
     * @param artists 작가 목록
     * @param artworks 작품 목록
     * @param pagination 페이징 정보
     * @return ArtworkListData DTO
     */
    public ArtworkListData toArtworkListData(List<ArtistSummary> artists, List<ArtworkSummary> artworks, Pagination pagination) {
        return ArtworkListData.builder()
                .artists(artists)
                .artworks(artworks)
                .pagination(pagination)
                .build();
    }
}
