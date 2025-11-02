package com.ssafy.yeobaek.common.code;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    // 성공
    SUCCESS("S0000", "정상 처리", HttpStatus.OK),
    CREATED("S0000", "정상 처리", HttpStatus.CREATED),

    // 공통 에러 (400 Bad Request)
    MISSING_REQUIRED_FIELD("S4001", "필수 입력값이 누락되었습니다", HttpStatus.BAD_REQUEST),

    // 공통 에러 (401 Unauthorized)
    UNAUTHORIZED("S4011", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("S4012", "토큰이 누락되었습니다", HttpStatus.UNAUTHORIZED),

    // 회원 관련 에러 (400 Bad Request)
    EMAIL_ALREADY_EXISTS("AU4001", "이미 사용 중인 이메일입니다", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD_FORMAT("AU4002", "비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("AU4003", "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORMAT("AU4004", "이메일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_USER_TYPE("AU4006", "유효하지 않은 회원 유형입니다", HttpStatus.BAD_REQUEST),
    INVALID_NICKNAME_LENGTH("AU4007", "닉네임은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    INVALID_BIRTH_DATE_FORMAT("AU4008", "생년월일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_GENDER("AU4009", "유효하지 않은 성별입니다", HttpStatus.BAD_REQUEST),

    // 회원 관련 에러 (401 Unauthorized)
    INVALID_CREDENTIALS("AU4011", "비밀번호가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("AU4012", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("AU4013", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED),

    // 회원 관련 에러 (404 Not Found)
    USER_NOT_FOUND("AU4041", "존재하지 않는 회원입니다", HttpStatus.NOT_FOUND),

    // 사용자 프로필 관련 에러 (400 Bad Request)
    INVALID_PROFILE_NICKNAME_LENGTH("US4002", "닉네임은 2~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    INVALID_INTRODUCTION_LENGTH("US4003", "작가 소개는 최대 250자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    INVALID_PROFILE_BIRTH_DATE("US4004", "생년월일 형식이 올바르지 않습니다", HttpStatus.BAD_REQUEST),
    INVALID_PROFILE_GENDER("US4005", "유효하지 않은 성별입니다", HttpStatus.BAD_REQUEST),

    // 사용자 프로필 관련 에러 (401 Unauthorized)
    INCORRECT_CURRENT_PASSWORD("US4011", "현재 패스워드가 일치하지 않습니다", HttpStatus.UNAUTHORIZED),

    // 사용자 프로필 관련 에러 (404 Not Found)
    PROFILE_USER_NOT_FOUND("US4041", "존재하지 않는 회원입니다", HttpStatus.NOT_FOUND),

    // 작품 관리 관련 에러 (400 Bad Request)
    ARTWORK_MIN_IMAGES("AW4001", "이미지는 최소 1장 이상 등록해야 합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_MAX_IMAGES("AW4002", "이미지는 최대 10장까지 등록 가능합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_INVALID_TITLE_LENGTH("AW4003", "작품명은 1~100자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_INVALID_DESCRIPTION_LENGTH("AW4004", "작품 설명은 최대 1000자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    ARTWORK_INVALID_CATEGORY("AW4006", "유효하지 않은 카테고리입니다", HttpStatus.BAD_REQUEST),
    ARTWORK_SIZE_FILTER_FORBIDDEN("AW4007", "작품 규격 필터는 카페 사장만 사용할 수 있습니다", HttpStatus.BAD_REQUEST),
    ARTWORK_PRIMARY_IMAGE_ONLY_ONE("AW4008", "대표 이미지는 1개만 선택해야 합니다", HttpStatus.BAD_REQUEST),

    // 작품 관리 관련 에러 (403 Forbidden)
    ARTWORK_FORBIDDEN_CREATE("AW4031", "작가만 작품을 등록할 수 있습니다", HttpStatus.FORBIDDEN),
    ARTWORK_FORBIDDEN("AW4032", "본인의 작품만 수정/삭제할 수 있습니다", HttpStatus.FORBIDDEN),

    // 작품 관리 관련 에러 (404 Not Found)
    ARTWORK_NOT_FOUND("AW4041", "존재하지 않는 작품입니다", HttpStatus.NOT_FOUND),

    // 작품 관리 관련 에러 (409 Conflict)
    ARTWORK_EXHIBITING_SIZE_CHANGE("AW4091", "전시 중인 작품은 규격을 변경할 수 없습니다", HttpStatus.CONFLICT),
    ARTWORK_CANNOT_DELETE_EXHIBITING("AW4092", "전시 중이거나 전시 예정인 작품은 삭제할 수 없습니다", HttpStatus.CONFLICT),

    // 카페 관련 에러 (400 Bad Request)
    CAFE_MIN_IMAGES("CF4001", "이미지는 최소 3장 이상 등록해야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_MAX_IMAGES("CF4002", "이미지는 최대 10장까지 등록 가능합니다", HttpStatus.BAD_REQUEST),
    CAFE_PRIMARY_IMAGE_ONLY_ONE("CF4003", "대표 이미지는 1개만 선택해야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_NAME_LENGTH("CF4004", "매장명은 1~15자 이내여야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_DESCRIPTION_LENGTH("CF4005", "카페 소개는 최대 500자까지 입력 가능합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_COMMISSION_RATE("CF4006", "수수료율은 0~15 사이여야 합니다", HttpStatus.BAD_REQUEST),
    CAFE_INVALID_HOURS_FORMAT("CF4007", "영업시간 형식이 올바르지 않습니다 (HH:mm)", HttpStatus.BAD_REQUEST),
    CAFE_LATITUDE_LONGITUDE_REQUIRED("CF4008", "거리순 정렬 시 위도/경도가 필요합니다", HttpStatus.BAD_REQUEST),

    // 카페 관련 에러 (403 Forbidden)
    CAFE_FORBIDDEN_CREATE("CF4031", "카페 사장만 등록할 수 있습니다", HttpStatus.FORBIDDEN),
    CAFE_FORBIDDEN_UPDATE_DELETE("CF4032", "본인의 카페만 수정/삭제할 수 있습니다", HttpStatus.FORBIDDEN),
    CAFE_SLOT_FILTER_FORBIDDEN("CF4033", "슬롯 규격 필터는 작가만 사용할 수 있습니다", HttpStatus.FORBIDDEN),
    CAFE_COMMISSION_FILTER_FORBIDDEN("CF4034", "수수료율 필터는 작가만 사용할 수 있습니다", HttpStatus.FORBIDDEN),
    CAFE_COMMISSION_SORT_FORBIDDEN("CF4035", "수수료율 정렬은 작가만 사용할 수 있습니다", HttpStatus.FORBIDDEN),

    // 카페 관련 에러 (404 Not Found)
    CAFE_NOT_FOUND("CF4041", "존재하지 않는 카페입니다", HttpStatus.NOT_FOUND),
    SLOT_NOT_FOUND("CF4042", "존재하지 않는 슬롯입니다", HttpStatus.NOT_FOUND),
    CAFE_CATEGORY_NOT_FOUND("CF4043", "존재하지 않는 카테고리입니다", HttpStatus.NOT_FOUND),

    // 카페 관련 에러 (409 Conflict)
    CAFE_ALREADY_EXISTS("CF4091", "이미 등록된 카페가 있습니다", HttpStatus.CONFLICT),
    SLOT_EXHIBITING_SIZE_CHANGE("CF4092", "전시 중인 슬롯은 규격을 변경할 수 없습니다", HttpStatus.CONFLICT),
    SLOT_CANNOT_DELETE_EXHIBITING("CF4093", "전시 중이거나 전시 예정인 슬롯은 삭제할 수 없습니다", HttpStatus.CONFLICT),

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
