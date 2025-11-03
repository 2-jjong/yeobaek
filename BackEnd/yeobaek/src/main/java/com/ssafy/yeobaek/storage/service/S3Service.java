package com.ssafy.yeobaek.storage.service;

import com.ssafy.yeobaek.storage.dto.request.PresignedUrlRequest;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrlResponse;

import java.util.List;

public interface S3Service {

    /**
     * 업로드용 Presigned URL 생성
     * @param request Presigned URL 생성 요청 정보 (저장 폴더, 개수)
     * @return Presigned URL 응답 리스트 (URL, 파일 키)
     */
    List<PresignedUrlResponse> generatePresignedUrlForUpload(PresignedUrlRequest request);

    /**
     * 다운로드/조회용 Presigned URL 생성
     * @param fileKey S3 파일 키
     * @return Presigned URL 문자열
     */
    String generatePresignedUrlForDownload(String fileKey);

    /**
     * 파일 삭제
     * @param fileKey 삭제할 S3 파일 키
     */
    void deleteFile(String fileKey);
}
