package com.example.shared.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthLoginRequest(
    val username: String,
    val password: String,
)
