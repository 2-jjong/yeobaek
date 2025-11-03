package com.ssafy.yeobaek.storage.controller;

import com.ssafy.yeobaek.common.dto.ResponseDto;
import com.ssafy.yeobaek.storage.dto.request.PresignedUrlRequest;
import com.ssafy.yeobaek.storage.dto.response.PresignedUrlResponse;
import com.ssafy.yeobaek.storage.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "파일 저장소", description = "S3 파일 저장소 관리 API")
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

    private final S3Service s3Service;

    @Operation(
            summary = "업로드용 Presigned URL 생성",
            description = "클라이언트가 S3에 직접 이미지를 업로드할 수 있는 Presigned URL을 생성합니다.\n\n" +
                    "**사용 방법:**\n" +
                    "1. 이 API를 호출하여 Presigned URL을 count 개수만큼 받습니다.\n" +
                    "2. 받은 각 URL로 PUT 요청을 보내 이미지를 업로드합니다.\n" +
                    "3. 업로드 완료 후 fileKey를 서버에 저장할 imageUrl로 사용합니다.\n\n" +
                    "**제약사항:**\n" +
                    "- count: 1~10개\n\n" +
                    "**유효 기간:** 10분"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": [{\"presignedUrl\": \"https://yeobaek-bucket.s3.ap-northeast-2.amazonaws.com/artwork/uuid1.jpg?X-Amz-Algorithm=...\", \"fileKey\": \"artwork/uuid1\"}, {\"presignedUrl\": \"https://yeobaek-bucket.s3.ap-northeast-2.amazonaws.com/artwork/uuid2.jpg?X-Amz-Algorithm=...\", \"fileKey\": \"artwork/uuid2\"}], \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "필수 입력값 누락",
                                            value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 저장 폴더",
                                            value = "{\"code\": \"ST4001\", \"message\": \"유효하지 않은 저장 폴더입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 파일 개수",
                                            value = "{\"code\": \"ST4002\", \"message\": \"파일 개수는 1개 이상 10개 이하여야 합니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @PostMapping("/presigned-url/upload")
    public ResponseEntity<ResponseDto<List<PresignedUrlResponse>>> generateUploadPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        List<PresignedUrlResponse> data = s3Service.generatePresignedUrlForUpload(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(data));
    }

    @Operation(
            summary = "[테스트용] 다운로드용 Presigned URL 생성",
            description = "**⚠️ 테스트용 API입니다.**\n\n" +
                    "업로드된 파일이 S3에 정상적으로 반영되었는지 확인하기 위한 테스트 API입니다.\n" +
                    "실제 서비스에서는 객체 조회 시 서버가 Presigned URL을 포함하여 응답합니다.\n\n" +
                    "**사용 방법:**\n" +
                    "1. DB에 저장된 fileKey를 파라미터로 전달합니다.\n" +
                    "2. 받은 Presigned URL로 GET 요청을 보내 파일을 다운로드합니다.\n\n" +
                    "**유효 기간:** 10분"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"data\": {\"presignedUrl\": \"https://yeobaek-bucket.s3.ap-northeast-2.amazonaws.com/artwork/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...\", \"fileKey\": \"artwork/a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5\"}, \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "필수 입력값 누락",
                                    value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @GetMapping("/presigned-url/download")
    public ResponseEntity<ResponseDto<PresignedUrlResponse>> generateDownloadPresignedUrl(
            @Parameter(description = "다운로드할 S3 파일 키", required = true)
            @RequestParam String fileKey
    ) {
        String presignedUrl = s3Service.generatePresignedUrlForDownload(fileKey);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success(PresignedUrlResponse.of(presignedUrl, fileKey)));
    }

    @Operation(
            summary = "파일 삭제",
            description = "S3에 저장된 파일을 삭제합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "정상 처리",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDto.class),
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S0000\", \"message\": \"정상 처리\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "필수 입력값 누락",
                                    value = "{\"code\": \"S4001\", \"message\": \"필수 입력값이 누락되었습니다\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "토큰 누락",
                                            value = "{\"code\": \"S4012\", \"message\": \"토큰이 누락되었습니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            value = "{\"code\": \"AU4012\", \"message\": \"유효하지 않은 토큰입니다\"}"
                                    ),
                                    @ExampleObject(
                                            name = "만료된 토큰",
                                            value = "{\"code\": \"AU4013\", \"message\": \"만료된 토큰입니다\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"code\": \"S5000\", \"message\": \"서버 내부 오류가 발생했습니다\"}"
                            )
                    )
            )
    })
    @DeleteMapping("/files")
    public ResponseEntity<ResponseDto<Void>> deleteFile(
            @Parameter(description = "삭제할 S3 파일 키", required = true)
            @RequestParam String fileKey
    ) {
        s3Service.deleteFile(fileKey);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ResponseDto.success());
    }
}
