package com.ssafy.yeobaek.cafe.mapper;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import com.ssafy.yeobaek.common.enums.ExhibitionStatus;
import com.ssafy.yeobaek.common.enums.WallStatus;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CafeMapper {

    /**
     * 카페 정보 DTO를 Cafe 엔티티로 변환
     * @param request 카페 정보
     * @param owner 카페 사장
     * @param coordinates 카페 위치 좌표
     * @return Cafe 엔티티
     */
    public Cafe toEntity(CreateCafeRequest request, User owner, Coordinates coordinates) {
        return Cafe.builder()
                .cafeOwner(owner)
                .cafeName(request.getCafeName())
                .address(request.getAddress())
                .latitude(coordinates.getLatitude())
                .longitude(coordinates.getLongitude())
                .description(request.getDescription())
                .contact(request.getContact())
                .businessStartTime(request.getBusinessHoursStart())
                .businessEndTime(request.getBusinessHoursEnd())
                .dailyVisitors(request.getDailyVisitors())
                .images(request.getImages())
                .build();
    }

    /**
     * 카페 벽 DTO를 DisplayWall 엔티티로 변환
     * @param request 카페 벽 정보
     * @param cafe 카페
     * @return DisplayWall 엔티티
     */
    public DisplayWall toDisplayWallEntity(CreateDisplayWallRequest request, Cafe cafe) {
        return DisplayWall.builder()
                .cafe(cafe)
                .wallName(request.getWallName())
                .startX(request.getStart().getX())
                .startY(request.getStart().getY())
                .endX(request.getEnd().getX())
                .endY(request.getEnd().getY())
                .status(request.getStatus())
                .build();
    }

    /**
     * DisplayWall 엔티티를 벽 DTO로 변환
     * @param displayWall 카페 벽 정보
     * @return Wall DTO
     */
    public Wall toWallDto(DisplayWall displayWall) {
        return new Wall(
                displayWall.getId(),
                displayWall.getWallName(),
                new Vec2(displayWall.getStartX(), displayWall.getStartY()),
                new Vec2(displayWall.getEndX(), displayWall.getEndY()),
                displayWall.getStatus() ? WallStatus.AVAILABLE : WallStatus.UNAVAILABLE
        );
    }

    /**
     * Cafe 엔티티와 DisplayWall 엔티티 리스트를 내 카페 정보 DTO로 변환
     * @param cafe 카페 정보
     * @param walls 카페 공간 정보
     * @return MyCafeData DTO
     */
    public MyCafeData toMyCafeData(Cafe cafe, List<DisplayWall> walls) {

        // 통계 계산
        long totalSlots = walls.size();
        long exhibitingSlots = walls.stream()
                .filter(w -> w.getSlot() != null && w.getSlot().getStatus() == ExhibitionStatus.EXHIBITING)
                .count();

        // 협업 작가 수 (임시)
        long collaboratingArtists = 15L;

        // 슬롯 정보
        List<DisplaySlot> displaySlots = walls.stream()
                .map(this::toDisplaySlot)
                .collect(Collectors.toList());

        // 요약 정보
        MyCafeSummary summary = new MyCafeSummary(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getAddress(),
                cafe.getImages().isEmpty() ? null : cafe.getImages().get(0),
                totalSlots,
                exhibitingSlots,
                collaboratingArtists,
                displaySlots
        );

        return new MyCafeData(summary);
    }

    private DisplaySlot toDisplaySlot(DisplayWall wall) {

        ExhibitionInfo exhibitionInfo = null;
        com.ssafy.yeobaek.cafe.entity.DisplaySlot slot = wall.getSlot();

        if (slot != null && (wall.getSlot().getStatus() == ExhibitionStatus.EXHIBITING
                || wall.getSlot().getStatus() == ExhibitionStatus.SCHEDULED)) {
            exhibitionInfo = new ExhibitionInfo(
                    1L,
                    "여름의 여백",
                    "https://example.com/artwork1.jpg",
                    "홍길동",
                    "2024-01-01",
                    "2024-03-31"
            );
        }

        return new DisplaySlot(
                wall.getId(),
                wall.getWallName(),
                wall.getWidthCm(),
                wall.getHeightCm(),
                slot != null ? slot.getStatus().toWallStatus() : WallStatus.UNAVAILABLE,
                slot != null ? slot.getCommissionRate() : null,
                slot != null ? slot.getRequirements() : null,
                exhibitionInfo
        );
    }

    /**
     * Cafe 엔티티와 관련 정보들을 카페 상세 정보 DTO로 변환
     * @param cafe 카페 정보
     * @param walls 카페 공간 정보
     * @param categories 카페 카테고리 목록
     * @return CafeDetailData DTO
     */
    public CafeDetailData toCafeDetailData(Cafe cafe, List<DisplayWall> walls, List<String> categories, List<String> images) {

        // 1. 벽 정보 -> 슬롯 상세 정보 DTO 목록으로 변환
        List<DisplaySlot> slotDetails = walls.stream()
            .map(this::toDisplaySlot)
            .collect(Collectors.toList());

        // 2. 카페 사장 정보 DTO 생성
        User ownerUser = cafe.getCafeOwner();
        // TODO: 실제 작가 평점 정보 연동 필요
        com.ssafy.yeobaek.user.dto.response.RatingStatsData ratingStats = new com.ssafy.yeobaek.user.dto.response.RatingStatsData(0, 0.0);
        CafeOwner owner = new CafeOwner(ownerUser.getId(), ownerUser.getNickname(), ownerUser.getProfileImageUrl(), ratingStats);

        // 3. 최종 CafeDetail DTO 조립
        CafeDetail cafeDetail = new CafeDetail(
            cafe.getId(),
            cafe.getCafeName(),
            cafe.getAddress(),
            cafe.getDescription(),
            cafe.getContact(),
            cafe.getBusinessStartTime(),
            cafe.getBusinessEndTime(),
            cafe.getDailyVisitors(),
            cafe.getViewCount(),
            false, // TODO: 실제 북마크 여부 정보 연동 필요
            categories,
            images,
            slotDetails,
            owner
        );

        return new CafeDetailData(cafeDetail);
    }
}