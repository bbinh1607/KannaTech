package com.example.shared.domain.repository.local

interface TokenRepository {
    fun getAccessToken(): String?

    fun saveAccessToken(
        accessToken: String?,
        refreshToken: String?,
    )

    fun clearAccessToken()

    fun getRefreshToken(): String?

    fun saveRefreshToken(token: String)

    fun clearRefreshToken()
}
