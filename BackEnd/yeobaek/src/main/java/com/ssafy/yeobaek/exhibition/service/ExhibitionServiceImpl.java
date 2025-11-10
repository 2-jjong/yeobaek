package com.ssafy.yeobaek.exhibition.service;

import com.ssafy.yeobaek.artwork.entity.Artwork;
import com.ssafy.yeobaek.artwork.repository.ArtworkRepository;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.repository.CafeRepository;
import com.ssafy.yeobaek.cafe.repository.DisplaySlotRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.enums.UserRole;
import com.ssafy.yeobaek.common.exception.CustomException;
import com.ssafy.yeobaek.exhibition.dto.request.CreateExhibitionRequest;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import com.ssafy.yeobaek.exhibition.mapper.ExhibitionMapper;
import com.ssafy.yeobaek.exhibition.repository.ExhibitionRepository;
import com.ssafy.yeobaek.user.entity.User;
import com.ssafy.yeobaek.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionMapper exhibitionMapper;

    // TODO: 순환 참조 문제 해결 후 Service 의존성 변경
    // 다른 도메인의 Repository 의존성
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final ArtworkRepository artworkRepository;
    private final DisplaySlotRepository displaySlotRepository;

    @Override
    @Transactional
    public void createExhibitionProposal(Long userId, CreateExhibitionRequest request) {

        // 1. 현재 사용자 조회
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseCode.USER_NOT_FOUND));

        // 2. Cafe, Artwork, DisplaySlot 조회
        Cafe cafe = cafeRepository.findById(request.getCafeId())
                .orElseThrow(() -> new CustomException(ResponseCode.CAFE_NOT_FOUND));

        Artwork artwork = artworkRepository.findById(request.getArtworkId())
                .orElseThrow(() -> new CustomException(ResponseCode.ARTWORK_NOT_FOUND));

        DisplaySlot displaySlot = displaySlotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new CustomException(ResponseCode.SLOT_NOT_FOUND));

        // 3. 슬롯이 해당 카페에 속하는지 확인
        if (!displaySlot.getWall().getCafe().getId().equals(cafe.getId())) {
            throw new CustomException(ResponseCode.SLOT_NOT_FOUND_IN_CAFE);
        }

        // 4. 전시 기간 검증
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (endDate.isBefore(startDate)) {
            throw new CustomException(ResponseCode.EXHIBITION_INVALID_EXHIBITION_DATE);
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new CustomException(ResponseCode.EXHIBITION_DATE_PAST);
        }

        // 5. 중복 체크 (같은 기간에 슬롯이나 작품이 이미 예약되어 있는지)
        boolean slotConflict = exhibitionRepository.existsConflictingSlot(request.getSlotId(), startDate, endDate);
        if (slotConflict) {
            throw new CustomException(ResponseCode.SLOT_ALREADY_RESERVED);
        }

        boolean artworkConflict = exhibitionRepository.existsConflictingArtwork(request.getArtworkId(), startDate, endDate);
        if (artworkConflict) {
            throw new CustomException(ResponseCode.ARTWORK_ALREADY_RESERVED);
        }

        // 6. 역할별 권한 검증 및 동의 상태 설정
        boolean cafeAgreed = false;
        boolean artistAgreed = false;

        if (currentUser.getUserType() == UserRole.CAFE_OWNER) {
            // 카페 사장이 제안
            if (!cafe.getCafeOwner().getId().equals(userId)) {
                throw new CustomException(ResponseCode.CAFE_FORBIDDEN_UPDATE_DELETE);
            }
            cafeAgreed = true;  // 제안자가 먼저 동의

        } else if (currentUser.getUserType() == UserRole.ARTIST) {
            // 작가가 제안
            if (!artwork.getArtist().getUser().getId().equals(userId)) {
                throw new CustomException(ResponseCode.ARTWORK_FORBIDDEN);
            }
            artistAgreed = true;  // 제안자가 먼저 동의

        } else {
            throw new CustomException(ResponseCode.FORBIDDEN);
        }

        // 7. Exhibition 엔티티 생성 (Mapper 사용)
        Exhibition exhibition = exhibitionMapper.toExhibition(
                request, cafe, artwork, displaySlot, cafeAgreed, artistAgreed
        );

        // 8. 저장
        exhibitionRepository.save(exhibition);
    }

    /**
     * 특정 작품의 현재 진행 중인 전시 조회
     * - 오늘 날짜 기준으로 진행 중인 전시 반환
     * - 전시가 없으면 Optional.empty() 반환
     *
     * @param artworkId 작품 ID
     * @return 현재 진행 중인 전시
     */
    @Override
    public Optional<Exhibition> getCurrentExhibitionByArtwork(Long artworkId) {
        LocalDate today = LocalDate.now();
        return exhibitionRepository.findCurrentExhibitionByArtworkId(artworkId, today);
    }
}
