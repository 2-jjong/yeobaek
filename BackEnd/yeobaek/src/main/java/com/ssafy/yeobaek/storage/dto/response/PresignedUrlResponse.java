package com.ssafy.yeobaek.storage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {

    private String presignedUrl;
    private String fileKey;

    public static PresignedUrlResponse of(String presignedUrl, String fileKey) {
        return PresignedUrlResponse.builder()
                .presignedUrl(presignedUrl)
                .fileKey(fileKey)
                .build();
    }
}
