package com.ssafy.yeobaeg.data.datasource.remote

import com.ssafy.yeobaeg.data.model.BaseResponse
import com.ssafy.yeobaeg.data.model.chat.response.ChatListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApiService {
    @GET("chats")
    suspend fun getChatList(
        @Query("sortBy") sortBy: String = "LATEST",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): BaseResponse<ChatListResponse>
}