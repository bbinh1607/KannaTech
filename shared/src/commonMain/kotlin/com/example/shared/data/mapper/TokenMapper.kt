package com.example.shared.data.mapper

import com.example.shared.core.mapper.BaseMapper
import com.example.shared.data.model.auth.AuthTokenResponse
import com.example.shared.domain.entity.TokenEntity

class TokenMapper : BaseMapper<AuthTokenResponse, TokenEntity> {
    override fun toEntity(response: AuthTokenResponse): TokenEntity =
        TokenEntity(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
        )
}
