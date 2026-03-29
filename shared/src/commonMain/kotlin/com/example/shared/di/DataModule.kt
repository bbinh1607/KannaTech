package com.example.shared.di

import com.example.shared.data.dataSource.local.TokenLocalDataSource
import com.example.shared.data.dataSource.local.TokenLocalDataSourceImpl
import com.example.shared.data.dataSource.remote.AuthRemoteDataSource
import com.example.shared.data.dataSource.remote.AuthRemoteDataSourceImpl
import com.example.shared.data.dataSource.remote.KtorClient
import com.example.shared.data.mapper.AuthMapper
import com.example.shared.data.repository.HabitRepositoryImpl
import com.example.shared.data.repositoryImpl.AuthRepositoryImpl
import com.example.shared.data.repositoryImpl.local.TokenRepositoryImpl
import com.example.shared.db.createDatabase
import com.example.shared.domain.repository.AuthRepository
import com.example.shared.domain.repository.HabitRepository
import com.example.shared.domain.repository.local.TokenRepository
import com.example.shared.domain.usecase.auth.AuthLoginUseCase
import com.example.shared.domain.usecase.auth.AuthRegisterUseCase
import com.example.shared.domain.usecase.token.ClearTokenUseCase
import com.example.shared.domain.usecase.token.GetAccessTokenUseCase
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val dataModule =
    module {
        single { Settings() }

        single<TokenLocalDataSource> { TokenLocalDataSourceImpl(get()) }

        single { KtorClient(get()).httpClient }

        single<TokenRepository> { TokenRepositoryImpl(get()) }

        single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get()) }

        single { AuthMapper() }

        single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }

        // SQLDelight
        single { createDatabase(get()) }
        
        // Habit Repository - Đã truyền thêm get() để Koin tự động tìm ReminderManager
        single<HabitRepository> { HabitRepositoryImpl(get(), get()) }

        factory { AuthLoginUseCase(get()) }
        factory { AuthRegisterUseCase(get()) }
        factory { GetAccessTokenUseCase(get()) }
        factory { ClearTokenUseCase(get()) }
    }

expect val platformModule: org.koin.core.module.Module
