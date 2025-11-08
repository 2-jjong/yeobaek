package com.ssafy.yeobaek.cafe.mapper;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.*;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import com.ssafy.yeobaek.common.enums.ExhibitionStatus;
import com.ssafy.yeobaek.common.enums.WallStatus;
import com.ssafy.yeobaek.user.dto.response.RatingStatsData;
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
    public Cafe toCafeEntity(CreateCafeRequest request, User owner, Coordinates coordinates) {
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
                .widthCm(request.getWidth())
                .heightCm(request.getHeight())
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
                displayWall.getStatus() ? displayWall.getSlot().getStatus().toWallStatus() : WallStatus.UNAVAILABLE
        );
    }

    /**
     * Cafe 엔티티와 DisplayWall 엔티티 리스트를 내 카페 정보 DTO로 변환
     * @param cafe 카페 정보
     * @param displayWalls 카페 공간 정보
     * @return MyCafeData DTO
     */
    public MyCafeData toMyCafeData(Cafe cafe, List<DisplayWall> displayWalls, String primaryImage) {

        // 통계 계산
        long totalSlots = displayWalls.size();
        long exhibitingSlots = displayWalls.stream()
                .filter(w -> w.getSlot() != null && w.getSlot().getStatus() == ExhibitionStatus.EXHIBITING)
                .count();

        // 협업 작가 수 (임시)
        long collaboratingArtists = 15L;

        // 벽 정보
        List<Wall> walls = displayWalls.stream()
                .map(this::toWallDto)
                .toList();

        // 슬롯 정보
        List<Slot> slots = displayWalls.stream()
                .map(this::toSlotDto)
                .collect(Collectors.toList());

        // 내 카페 정보
        MyCafe myCafe = new MyCafe(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getAddress(),
                primaryImage,
                totalSlots,
                exhibitingSlots,
                collaboratingArtists,
                walls,
                slots
        );

        return new MyCafeData(myCafe);
    }

    /**
     * DisplayWall 엔티티를 슬롯 DTO로 변환
     * @param wall 벽 정보
     * @return Slot DTO
     */
    private Slot toSlotDto(DisplayWall wall) {

        ExhibitionInfo exhibitionInfo = null;
        DisplaySlot slot = wall.getSlot();

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

        return new Slot(
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
     * Cafe 엔티티를 CafeSummary DTO로 변환
     * @param cafe 카페 엔티티
     * @param distance 사용자 위치로부터의 거리
     * @param categories 카페 카테고리 이름 리스트
     * @return CafeSummary DTO
     */
    public CafeSummary toCafeSummary(Cafe cafe, Double distance, List<String> categories, String primaryImageUrl) {
        int totalSlotCount = cafe.getDisplayWalls().size();
        int availableSlotCount = (int) cafe.getDisplayWalls().stream().filter(DisplayWall::getStatus).count();

        return new CafeSummary(
                cafe.getId(),
                cafe.getCafeName(),
                cafe.getAddress(),
                cafe.getLatitude(),
                cafe.getLongitude(),
                primaryImageUrl,
                availableSlotCount,
                totalSlotCount,
                distance,
                categories
        );
    }

    /**
     * Cafe 엔티티와 관련 정보들을 카페 상세 정보 DTO로 변환
     * @param cafe 카페 정보
     * @param walls 카페 공간 정보
     * @param categories 카페 카테고리 목록
     * @return CafeDetailData DTO
     */
    public CafeDetailData toCafeDetailData(Cafe cafe, List<DisplayWall> walls, List<String> categories, List<String> images, String profileImage) {

        // 1. 벽 정보 -> 슬롯 상세 정보 DTO 목록으로 변환
        List<Slot> slotDetails = walls.stream()
                .map(this::toSlotDto)
                .collect(Collectors.toList());

        // 2. 카페 사장 정보 DTO 생성
        User ownerUser = cafe.getCafeOwner();
        // TODO: 실제 작가 평점 정보 연동 필요
        RatingStatsData ratingStats = new RatingStatsData(0, 0.0);
        CafeOwner owner = new CafeOwner(ownerUser.getId(), ownerUser.getNickname(), profileImage, ratingStats);

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

    /**
     * DisplaySlot 엔티티를 DTO로 변환
     * @param slot 슬롯 정보
     * @return Slot DTO
     */
    public Slot toSlotDto(DisplaySlot slot){
        return Slot.builder()
                .wallId(slot.getId())
                .wallName(slot.getWall().getWallName())
                .width(slot.getWall().getWidthCm())
                .status(slot.getStatus().toWallStatus())
                .height(slot.getWall().getHeightCm())
                .commissionRate(slot.getCommissionRate())
                .requirements(slot.getRequirements())
                .build();
    }
}