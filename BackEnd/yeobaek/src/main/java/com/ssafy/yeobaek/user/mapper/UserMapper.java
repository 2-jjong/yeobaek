package com.ssafy.yeobaek.user.mapper;

import com.ssafy.yeobaek.user.dto.response.ArtistData;
import com.ssafy.yeobaek.user.dto.response.CafeData;
import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
import com.ssafy.yeobaek.user.dto.response.UserData;
import com.ssafy.yeobaek.user.entity.Artist;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * User 도메인 엔티티와 DTO 간의 변환을 담당하는 매퍼
 */
@Component
public class UserMapper {

    /**
     * User 엔티티를 UserData DTO로 변환
     * @param user User 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @return UserData DTO
     */
    public UserData toUserData(User user, String profileImageUrl) {
        return UserData.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .profileImageUrl(profileImageUrl)
                .birthDate(user.getBirthDate().toString())
                .gender(user.getGender())
                .bookmarkCount(37) // TODO: 실제 북마크 수로 변경
                .build();
    }

    /**
     * User 엔티티를 공개 프로필용 UserData DTO로 변환
     * 공개되는 정보: userId, nickname, userType, profileImageUrl만 포함
     * @param user User 엔티티
     * @param profileImageUrl 변환된 프로필 이미지 URL (presigned URL)
     * @return 공개 프로필용 UserData DTO
     */
    public UserData toPublicUserData(User user, String profileImageUrl) {
        return UserData.builder()
                .userId(user.getId())
                .email(null)
                .nickname(user.getNickname())
                .userType(user.getUserType())
                .profileImageUrl(profileImageUrl)
                .birthDate(null)
                .gender(null)
                .bookmarkCount(null)
                .build();
    }

    /**
     * Artist 엔티티를 ArtistData DTO로 변환
     * @param artist Artist 엔티티
     * @param artworkCount 작품 수
     * @param exhibitingCount 전시 중인 작품 수
     * @param totalExhibitedCount 총 전시한 작품 수
     * @param totalReviews 총 리뷰 수
     * @param averageRating 평균 평점
     * @return ArtistData DTO
     */
    public ArtistData toArtistData(
            Artist artist,
            int artworkCount,
            int exhibitingCount,
            int totalExhibitedCount,
            int totalReviews,
            double averageRating
    ) {
        RatingStatsData ratingStats = RatingStatsData.builder()
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .build();

        return ArtistData.builder()
                .introduction(artist.getIntroduction())
                .category(artist.getCategory())
                .artworkCount(artworkCount)
                .exhibitingCount(exhibitingCount)
                .totalExhibitedCount(totalExhibitedCount)
                .ratingStats(ratingStats)
                .build();
    }

    /**
     * 더미 CafeData 생성
     * TODO: Cafe 엔티티 및 Repository 구현 후 toCafeData(Cafe cafe) 메서드로 변경
     * @return 더미 CafeData
     */
    public CafeData toDummyCafeData() {
        RatingStatsData ratingStats = RatingStatsData.builder()
                .totalReviews(156)
                .averageRating(4.7)
                .build();

        return CafeData.builder()
                .cafeId(1L)
                .cafeName("아트 갤러리 카페")
                .address("서울시 강남구 테헤란로 123")
                .description("작가들의 작품을 전시하고 감상할 수 있는 복합 문화 공간입니다.")
                .contact("02-1234-5678")
                .businessHoursStart("09:00")
                .businessHoursEnd("22:00")
                .ratingStats(ratingStats)
                .build();
    }

    // TODO: Cafe 엔티티 구현 후 아래 메서드 추가
    // /**
    //  * Cafe 엔티티를 CafeData DTO로 변환
    //  * @param cafe Cafe 엔티티
    //  * @return CafeData DTO
    //  */
    // public CafeData toCafeData(Cafe cafe) {
    //     RatingStatsData ratingStats = RatingStatsData.builder()
    //             .totalReviews(/* 실제 평가 수 */)
    //             .averageRating(/* 실제 평균 평점 */)
    //             .build();
    //
    //     return CafeData.builder()
    //             .cafeId(cafe.getId())
    //             .cafeName(cafe.getName())
    //             .address(cafe.getAddress())
    //             .description(cafe.getDescription())
    //             .contact(cafe.getContact())
    //             .businessHoursStart(cafe.getBusinessHoursStart().toString())
    //             .businessHoursEnd(cafe.getBusinessHoursEnd().toString())
    //             .ratingStats(ratingStats)
    //             .build();
    // }
}
