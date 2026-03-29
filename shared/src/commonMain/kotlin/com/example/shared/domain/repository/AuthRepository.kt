package com.example.shared.domain.repository

import com.example.shared.core.response.DataState
import com.example.shared.domain.entity.TokenEntity
import com.example.shared.domain.entity.UserEntity
import com.example.shared.domain.param.LoginParams
import com.example.shared.domain.param.RegisterParams

interface AuthRepository {
    suspend fun login(params: LoginParams): DataState<TokenEntity>

    suspend fun register(params: RegisterParams): DataState<UserEntity>
}
