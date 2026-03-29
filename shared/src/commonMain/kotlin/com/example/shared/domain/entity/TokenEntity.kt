package com.example.shared.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class TokenEntity(
    val accessToken: String?,
    val refreshToken: String?,
)
