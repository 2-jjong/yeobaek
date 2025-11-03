package com.ssafy.yeobaeg.domain.repository

import com.ssafy.yeobaeg.domain.model.BookmarkArtwork
import com.ssafy.yeobaeg.domain.model.BookmarkCafe
import com.ssafy.yeobaeg.domain.model.Profile

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
    suspend fun getBookmarkArtworkList(page: Int, size: Int): Result<BookmarkArtwork>
    suspend fun getBookmarkCafeList(page: Int, size: Int): Result<BookmarkCafe>

}

