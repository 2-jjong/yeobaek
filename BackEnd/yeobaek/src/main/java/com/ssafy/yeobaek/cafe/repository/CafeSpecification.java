package com.ssafy.yeobaek.cafe.repository;

import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.DisplaySlot;
import com.ssafy.yeobaek.cafe.entity.DisplayWall;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 카페 검색을 위한 JPA Specification
 */
public class CafeSpecification {

    /**
     * 검색 필터 조건 생성
     *
     * @param keyword       검색 키워드 (카페명)
     * @param minWidth      최소 너비
     * @param maxWidth      최대 너비
     * @param minHeight     최소 높이
     * @param maxHeight     최대 높이
     * @param minCommission 최소 수수료
     * @param maxCommission 최대 수수료
     * @return Specification<Cafe> 검색 조건
     */
    public static Specification<Cafe> withFilters(
            String keyword,
            Integer minWidth,
            Integer maxWidth,
            Integer minHeight,
            Integer maxHeight,
            Double minCommission,
            Double maxCommission
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // DisplayWall, DisplaySlot을 조인하여 필터링에 사용
            Join<Cafe, DisplayWall> wallJoin = root.join("displayWalls", JoinType.LEFT);
            Join<DisplayWall, DisplaySlot> slotJoin = wallJoin.join("slot", JoinType.LEFT);

            // 키워드 검색 (카페명)
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("cafeName")), "%" + keyword.toLowerCase() + "%"));
            }

            // 규격 필터 - 가로
            if (minWidth != null) {
                predicates.add(cb.ge(wallJoin.get("widthCm"), minWidth));
            }
            if (maxWidth != null) {
                predicates.add(cb.le(wallJoin.get("widthCm"), maxWidth));
            }

            // 규격 필터 - 세로
            if (minHeight != null) {
                predicates.add(cb.ge(wallJoin.get("heightCm"), minHeight));
            }
            if (maxHeight != null) {
                predicates.add(cb.le(wallJoin.get("heightCm"), maxHeight));
            }

            // 수수료 필터
            if (minCommission != null) {
                predicates.add(cb.ge(slotJoin.get("commissionRate"), minCommission));
            }
            if (maxCommission != null) {
                predicates.add(cb.le(slotJoin.get("commissionRate"), maxCommission));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}