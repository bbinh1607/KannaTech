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
            username = params.username,
            email = params.email,
            password = params.password,
            imageUrl = params.imageUrl,
            address = params.address,
            phone = params.phone,
        )

    // ===== RESPONSE → ENTITY =====

    fun toTokenEntity(response: AuthTokenResponse): TokenEntity =
        AuthTokenResponse(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        ).let {
            TokenEntity(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
            )
        }

    fun toUserEntity(response: UserResponse): UserEntity =
        UserEntity(
            id = response.id ?: "",
            name = response.username, // Sử dụng username làm name vì server không trả về trường name
            email = response.email ?: "",
            password = response.password,
            address = response.address ?: "",
            phone = response.phone ?: "",
            role =
                RoleEntity(
                    id = response.role?.id ?: "",
                    name = response.role?.name ?: "guest",
                    rank = response.role?.rank ?: 0,
                    description = response.role?.description ?: "",
                ),
        )
}
