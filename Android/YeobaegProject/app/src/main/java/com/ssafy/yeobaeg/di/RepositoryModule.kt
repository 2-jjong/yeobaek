package com.ssafy.yeobaeg.di

import com.ssafy.yeobaeg.data.repository.ArtRepositoryImpl
import com.ssafy.yeobaeg.data.repository.AuthRepositoryImpl
import com.ssafy.yeobaeg.data.repository.CafeRepositoryImpl
import com.ssafy.yeobaeg.data.repository.ProfileRepositoryImpl
import com.ssafy.yeobaeg.data.repository.RecentSearchRepositoryImpl
import com.ssafy.yeobaeg.domain.repository.ArtRepository
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import com.ssafy.yeobaeg.domain.repository.CafeRepository
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
import com.ssafy.yeobaeg.domain.repository.RecentSearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindCafeRepository(
        cafeRepositoryImpl: CafeRepositoryImpl
    ): CafeRepository

    @Binds
    @Singleton
    abstract fun bindRecentSearchRepository(
        recentSearchRepositoryImpl: RecentSearchRepositoryImpl
    ): RecentSearchRepository




    @Binds
    @Singleton
    abstract fun bindArtRepository(
        artRepositoryImpl: ArtRepositoryImpl
    ): ArtRepository
}

