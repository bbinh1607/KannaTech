package com.example.shared.data.mapper

import com.example.shared.data.model.auth.AuthLoginRequest
import com.example.shared.data.model.auth.AuthRegisterRequest
import com.example.shared.data.model.auth.AuthTokenResponse
import com.example.shared.data.model.user.UserResponse
import com.example.shared.domain.entity.RoleEntity
import com.example.shared.domain.entity.TokenEntity
import com.example.shared.domain.entity.UserEntity
import com.example.shared.domain.param.LoginParams
import com.example.shared.domain.param.RegisterParams

class AuthMapper {
    // ===== PARAM → REQUEST =====

    fun fromLoginParams(params: LoginParams): AuthLoginRequest =
        AuthLoginRequest(
            username = params.username,
            password = params.password,
        )

    fun fromRegisterParams(params: RegisterParams): AuthRegisterRequest =
        AuthRegisterRequest(
            name = params.name,
            email = params.email,
            password = params.password,
            address = params.address,
            phone = params.phone,
        )

    // ===== RESPONSE → ENTITY =====

    fun toTokenEntity(response: AuthTokenResponse): TokenEntity =
        TokenEntity(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )

    fun toUserEntity(response: UserResponse): UserEntity =
        UserEntity(
            id = response.id,
            name = response.name,
            email = response.email,
            password = response.password,
            address = response.address ?: "",
            phone = response.phone ?: "",
            role =
                RoleEntity(
                    id = response.role.id,
                    name = response.role.name,
                    rank = response.role.rank,
                    description = response.role.description ?: "",
                ),
        )
}
