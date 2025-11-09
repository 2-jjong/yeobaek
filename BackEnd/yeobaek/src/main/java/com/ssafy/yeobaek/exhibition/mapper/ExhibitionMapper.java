package com.ssafy.yeobaek.exhibition.mapper;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import org.springframework.stereotype.Component;

@Component
public class ExhibitionMapper {

    /**
     * CreateExhibitionRequest를 Exhibition 엔티티로 변환
     * @param request 전시 제안 요청 DTO
     * @param cafe 카페 엔티티
     * @param artwork 작품 엔티티
     * @param displaySlot 전시 슬롯 엔티티
     * @param cafeAgreed 카페 사장 동의 여부
     * @param artistAgreed 작가 동의 여부
     * @return Exhibition 엔티티
     */
    public Exhibition toExhibition(
            CreateExhibitionRequest request,
            Cafe cafe,
            Artwork artwork,
            DisplaySlot displaySlot,
            boolean cafeAgreed,
            boolean artistAgreed
    ) {
        return Exhibition.builder()
                .cafe(cafe)
                .displaySlot(displaySlot)
                .artwork(artwork)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .commissionRate(request.getCommissionRate())
                .requirements(request.getRequirements())
                .state(ExhibitionState.PENDING)
                .cafeAgreed(cafeAgreed)
                .artistAgreed(artistAgreed)
                .build();
    }
}
