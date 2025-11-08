package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.response.Category;
import com.ssafy.yeobaek.cafe.dto.response.CategoryListData;
import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import com.ssafy.yeobaek.cafe.repository.CafeCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeCategoryServiceImpl implements CafeCategoryService {

    private final CafeCategoryRepository cafeCategoryRepository;

    /**
     * ID 목록으로 카테고리 목록 조회
     * @param categoryIds 카테고리 ID 목록
     * @return 카테고리 엔티티 목록
     */
    @Override
    public List<CafeCategory> findCategoriesByIds(List<Long> categoryIds) {
        return categoryIds.stream()
                .map(cafeCategoryRepository::getReferenceById)
                .collect(Collectors.toList());
    }

    /**
     * 모든 카페 카테고리 목록 조회
     * @return 카페 카테고리 목록
     */
    @Override
    public CategoryListData getAllCafeCategories() {
        List<Category> categories = cafeCategoryRepository.findAll().stream()
                .sorted(Comparator.comparing(CafeCategory::getId))
                .map(cafeCategory -> new Category(cafeCategory.getId(), cafeCategory.getCategoryName()))
                .collect(Collectors.toList());
        return new CategoryListData(categories);
    }
}
