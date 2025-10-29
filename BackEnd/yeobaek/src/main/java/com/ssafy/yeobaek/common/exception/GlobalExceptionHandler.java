package com.ssafy.yeobaek.common.exception;

import com.ssafy.yeobaek.common.code.ResponseCode;
import com.ssafy.yeobaek.common.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<Void>> handleCustomException(CustomException e) {
        ResponseCode responseCode = e.getResponseCode();
        return ResponseEntity
            .status(responseCode.getHttpStatus())
            .body(ResponseDto.error(responseCode));
    }

    // Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException e) {
        // 첫 번째 에러 메시지 추출
        FieldError fieldError = e.getBindingResult().getFieldErrors().get(0);
        String errorMessage = fieldError.getDefaultMessage();

        // 에러 메시지에 따라 적절한 ResponseCode 매핑
        ResponseCode responseCode = mapValidationMessageToResponseCode(errorMessage);

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ResponseDto.error(responseCode));
    }

    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Void>> handleException(Exception e) {
        return ResponseEntity
            .status(ResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus())
            .body(ResponseDto.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }

    // Validation 에러 메시지를 ResponseCode로 매핑
    private ResponseCode mapValidationMessageToResponseCode(String message) {
        if (message.contains("이메일 형식")) {
            return ResponseCode.INVALID_EMAIL_FORMAT;
        } else if (message.contains("비밀번호는 8자 이상")) {
            return ResponseCode.INVALID_PASSWORD_FORMAT;
        } else if (message.contains("회원 유형")) {
            return ResponseCode.INVALID_USER_TYPE;
        } else if (message.contains("닉네임은 2~15자")) {
            return ResponseCode.INVALID_NICKNAME_LENGTH;
        } else if (message.contains("생년월일 형식")) {
            return ResponseCode.INVALID_BIRTH_DATE_FORMAT;
        } else if (message.contains("성별")) {
            return ResponseCode.INVALID_GENDER;
        } else if (message.contains("필수 입력값")) {
            return ResponseCode.MISSING_REQUIRED_FIELD;
        } else {
            return ResponseCode.MISSING_REQUIRED_FIELD;
        }
    }
}
