package com.ssafy.yeobaeg.domain.model

/**
 * 인증 관련 이벤트
 */
sealed class AuthEvent {
    /**
     * 토큰이 만료되어 로그아웃 처리가 필요한 경우
     */
    data object TokenExpired : AuthEvent()

    data object LoggedOut : AuthEvent()
}

