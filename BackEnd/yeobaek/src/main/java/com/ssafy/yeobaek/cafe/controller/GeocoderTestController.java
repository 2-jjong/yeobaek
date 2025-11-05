package com.ssafy.yeobaek.cafe.controller;

import com.ssafy.yeobaek.cafe.dto.response.Coordinates;
import com.ssafy.yeobaek.cafe.service.GeocoderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "테스트 컨트롤러", description = "기능 테스트용 API")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class GeocoderTestController {

    private final GeocoderService geocoderService;

    @Operation(summary = "지오코딩 테스트", description = "주소를 좌표로 변환하는 기능을 테스트합니다.")
    @GetMapping("/geocode")
    public ResponseEntity<Coordinates> geocodeAddress(@RequestParam String address) {
        Coordinates coordinates = geocoderService.getCoordinates(address);
        if (coordinates != null) {
            return ResponseEntity.ok(coordinates);
        }
        return ResponseEntity.notFound().build();
    }
}
