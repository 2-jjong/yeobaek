package com.ssafy.yeobaek.cafe.service;

import com.ssafy.yeobaek.cafe.dto.request.CafeImageRequest;
import com.ssafy.yeobaek.cafe.entity.Cafe;
import com.ssafy.yeobaek.cafe.entity.CafeImage;
import com.ssafy.yeobaek.cafe.repository.CafeImageRepository;
import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CafeImageServiceImpl implements CafeImageService {

    private final CafeImageRepository cafeImageRepository;

    @Override
    public void validatePrimaryImage(List<CafeImageRequest> images) {
        long primaryImageCount = images.stream().filter(CafeImageRequest::getIsPrimary).count();
        if (primaryImageCount != 1) {
            throw new CustomException(ResponseCode.CAFE_PRIMARY_IMAGE_ONLY_ONE);
        }
    }

    @Override
    public void saveImages(Cafe cafe, List<CafeImageRequest> images) {
        images.forEach(imageRequest -> {
            CafeImage cafeImage = CafeImage.builder()
                    .cafe(cafe)
                    .imageUrl(imageRequest.getImageUrl())
                    .isPrimary(imageRequest.getIsPrimary())
                    .displayOrder(imageRequest.getDisplayOrder())
                    .build();
            cafeImageRepository.save(cafeImage);
        });
    }
}
