package com.ssafy.yeobaek.cafe.mapper;

import com.ssafy.yeobaek.cafe.dto.Vec2;
import com.ssafy.yeobaek.cafe.dto.request.CreateCafeRequest;
import com.ssafy.yeobaek.cafe.dto.request.CreateDisplayWallRequest;
import com.ssafy.yeobaek.cafe.dto.response.Coordinates;
import com.ssafy.yeobaek.cafe.dto.response.Wall;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import com.ssafy.yeobaek.common.enums.WallStatus;
import com.ssafy.yeobaek.user.entity.User;
import org.springframework.stereotype.Component;

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

    public Wall toWallDto(DisplayWall displayWall) {
        return new Wall(
                displayWall.getId(),
                displayWall.getWallName(),
                new Vec2(displayWall.getStartX(), displayWall.getStartY()),
                new Vec2(displayWall.getEndX(), displayWall.getEndY()),
                displayWall.getStatus() ? WallStatus.AVAILABLE : WallStatus.UNAVAILABLE
        );
    }
}