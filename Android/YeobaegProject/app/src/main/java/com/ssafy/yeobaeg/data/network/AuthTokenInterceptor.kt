package com.ssafy.yeobaeg.data.network

import com.ssafy.yeobaeg.data.datasource.local.TokenDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 모든 API 요청에 Authorization 헤더를 자동으로 추가하는 Interceptor
 */
@Singleton
class AuthTokenInterceptor @Inject constructor(
    private val tokenDataSource: TokenDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 로그인, 회원가입, refresh 요청은 토큰을 추가하지 않음
        val url = originalRequest.url.toString()
        if (url.contains("/auth/login") || 
            url.contains("/auth/signup") || 
            url.contains("/auth/refresh")) {
            return chain.proceed(originalRequest)
        }

        // DataStore에서 토큰 가져오기 (코루틴 블로킹)
        val accessToken = runBlocking {
            tokenDataSource.getAccessToken()
        }

        // 토큰이 있으면 헤더에 추가
        val requestWithToken = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(requestWithToken)
    }
}

