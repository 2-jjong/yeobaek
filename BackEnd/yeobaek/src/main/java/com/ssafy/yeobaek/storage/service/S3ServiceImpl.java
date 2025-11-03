package com.ssafy.yeobaek.storage.service;

import com.ssafy.yeobaek.storage.dto.request.PresignedUrlRequest;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrlResponse;
import com.ssafy.yeobaek.common.enums.StorageFolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private static final Long PRESIGNED_URL_EXPIRATION_MINUTES = 10L;

    /**
     * 업로드용 Presigned URL 생성
     * @param request Presigned URL 생성 요청 정보 (저장 폴더, 개수)
     * @return Presigned URL 응답 리스트 (URL, 파일 키, 만료 시간)
     */
    @Override
    public List<PresignedUrlResponse> generatePresignedUrlForUpload(PresignedUrlRequest request) {
        List<PresignedUrlResponse> responses = new ArrayList<>();

        for (int i = 0; i < request.getCount(); i++) {
            String fileKey = generateFileKey(StorageFolder.valueOf(request.getFolder()));

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
                    .putObjectRequest(putObjectRequest)
                    .build();

            String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

            log.info("Generated presigned URL for upload: {}", fileKey);

            responses.add(PresignedUrlResponse.of(presignedUrl, fileKey));
        }

        return responses;
    }

    /**
     * 다운로드/조회용 Presigned URL 생성
     * @param fileKey S3 파일 키
     * @return Presigned URL 문자열
     */
    @Override
    public String generatePresignedUrlForDownload(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRATION_MINUTES))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();

        log.info("Generated presigned URL for download: {}", fileKey);

        return presignedUrl;
    }

    /**
     * 파일 삭제
     * @param fileKey 삭제할 S3 파일 키
     */
    @Override
    public void deleteFile(String fileKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        log.info("Deleted file from S3: {}", fileKey);
    }

    /**
     * 유니크한 파일 키 생성
     * @param folder 저장할 폴더
     * @return UUID 기반 유니크한 파일 키 (폴더/UUID)
     */
    private String generateFileKey(StorageFolder folder) {
        String uuid = UUID.randomUUID().toString();
        return String.format("%s/%s", folder.getPath(), uuid);
    }
}
