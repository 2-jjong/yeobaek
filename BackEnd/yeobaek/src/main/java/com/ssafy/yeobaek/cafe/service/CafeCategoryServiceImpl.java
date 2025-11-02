package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.entity.CafeCategory;
import com.ssafy.yeobaek.cafe.repository.CafeCategoryRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeCategoryServiceImpl implements CafeCategoryService {

    private final CafeCategoryRepository cafeCategoryRepository;

    @Override
    public List<CafeCategory> findCategoriesByIds(List<Long> categoryIds) {
        return categoryIds.stream()
                .map(categoryId -> cafeCategoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CustomException(ResponseCode.CAFE_CATEGORY_NOT_FOUND)))
                .collect(Collectors.toList());
    }
}
