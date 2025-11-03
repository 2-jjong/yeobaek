package com.ssafy.yeobaek.storage.dto.request;

import com.ssafy.yeobaek.common.enums.StorageFolder;
import com.ssafy.yeobaek.common.validation.ValidEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlRequest {

    @NotBlank(message = "MISSING_REQUIRED_FIELD")
    @ValidEnum(enumClass = StorageFolder.class, message = "INVALID_STORAGE_FOLDER")
    private String folder;

    @NotNull(message = "MISSING_REQUIRED_FIELD")
    @Min(value = 1, message = "INVALID_FILE_COUNT")
    @Max(value = 10, message = "INVALID_FILE_COUNT")
    private Integer count;
}
