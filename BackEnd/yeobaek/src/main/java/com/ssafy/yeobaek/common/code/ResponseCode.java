package com.ssafy.yeobaek.common.code;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    // 성공
    SUCCESS("S0000", "정상 처리", HttpStatus.OK),
    CREATED("S0000", "정상 처리", HttpStatus.CREATED),

    // 인증 관련 에러 (400 Bad Request)
    EMAIL_ALREADY_EXISTS("AU4001", "이미 사용 중인 이메일입니다", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("AU4002", "비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("AU4003", "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("AU4004", "이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("AU4005", "필수 입력값이 누락되었습니다", HttpStatus.BAD_REQUEST),
    INVALID_USER_TYPE("AU4006", "유효하지 않은 회원 유형입니다", HttpStatus.BAD_REQUEST),
    INVALID_NICKNAME_LENGTH("AU4007", "닉네임은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    INVALID_BIRTH_DATE_FORMAT("AU4008", "생년월일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_GENDER("AU4009", "유효하지 않은 성별입니다", HttpStatus.BAD_REQUEST),

    // 인증 관련 에러 (401 Unauthorized)
    INVALID_CREDENTIALS("AU4011", "이메일 또는 비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AU4013", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AU4014", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED),

    // 인증 관련 에러 (404 Not Found)
    USER_NOT_FOUND("AU4041", "존재하지 않는 회원입니다", HttpStatus.NOT_FOUND),

    // 서버 에러 (500 Internal Server Error)
    INTERNAL_SERVER_ERROR("S5000", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ResponseCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
