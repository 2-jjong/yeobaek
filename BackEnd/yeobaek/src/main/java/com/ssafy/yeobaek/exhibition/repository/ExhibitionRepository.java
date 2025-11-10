package com.ssafy.yeobaek.exhibition.repository;

import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.common.enums.ExhibitionState;
import com.ssafy.yeobaek.exhibition.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    /**
     * 특정 슬롯의 특정 기간에 중복 전시가 있는지 확인
     * @param slotId 슬롯 ID
     * @param startDate 전시 시작일
     * @param endDate 전시 종료일
     * @return 중복 여부
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Exhibition e " +
           "WHERE e.displaySlot.id = :slotId " +
           "AND e.state IN ('APPROVED', 'ONGOING') " +
           "AND ((e.startDate <= :endDate AND e.endDate >= :startDate))")
    boolean existsConflictingSlot(
            @Param("slotId") Long slotId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 작품의 특정 기간에 중복 전시가 있는지 확인
     * @param artworkId 작품 ID
     * @param startDate 전시 시작일
     * @param endDate 전시 종료일
     * @return 중복 여부
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Exhibition e " +
           "WHERE e.artwork.id = :artworkId " +
           "AND e.state IN ('APPROVED', 'ONGOING') " +
           "AND ((e.startDate <= :endDate AND e.endDate >= :startDate))")
    boolean existsConflictingArtwork(
            @Param("artworkId") Long artworkId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 특정 작품의 오늘 날짜 기준 진행 중인 전시 조회
     * @param artworkId 작품 ID
     * @param today 오늘 날짜
     * @return 진행 중인 전시
     */
    @Query("SELECT e FROM Exhibition e " +
           "WHERE e.artwork.id = :artworkId " +
           "AND e.state = 'ONGOING' " +
           "AND e.startDate <= :today " +
           "AND e.endDate >= :today")
    Optional<Exhibition> findCurrentExhibitionByArtworkId(
            @Param("artworkId") Long artworkId,
            @Param("today") LocalDate today
    );

    /**
     * 특정 슬롯에 대해 현재 진행 중이거나 예정된 전시 중 가장 빠른 전시를 조회
     * @param displaySlot 슬롯 엔티티
     * @param states 전시 상태 목록 (ONGOING, APPROVED)
     * @return 조건에 맞는 첫 번째 전시
     */
    Optional<Exhibition> findFirstByDisplaySlotAndStateInOrderByStartDateAsc(DisplaySlot displaySlot, List<ExhibitionState> states);
}
