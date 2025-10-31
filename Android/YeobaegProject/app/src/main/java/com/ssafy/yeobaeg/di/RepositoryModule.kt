package com.ssafy.yeobaeg.di

import com.ssafy.yeobaeg.data.repository.AuthRepositoryImpl
import com.ssafy.yeobaeg.data.repository.ProfileRepositoryImpl
import com.ssafy.yeobaeg.domain.repository.AuthRepository
import com.ssafy.yeobaeg.domain.repository.ProfileRepository
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
}

