package com.example.shared.data.repositoryImpl.local

import com.example.shared.data.dataSource.local.TokenLocalDataSource
import com.example.shared.domain.repository.local.TokenRepository

class TokenRepositoryImpl(
    private val tokenLocalDataSource: TokenLocalDataSource,
) : TokenRepository {
    override fun getAccessToken(): String? = tokenLocalDataSource.getAccessToken()

    override fun saveAccessToken(
        accessToken: String?,
        refreshToken: String?,
    ) {
        tokenLocalDataSource.saveAccessToken(accessToken)
        tokenLocalDataSource.saveRefreshToken(refreshToken)
    }

    override fun clearAccessToken() {
        tokenLocalDataSource.clearAll()
    }

    override fun getRefreshToken(): String? = tokenLocalDataSource.getRefreshToken()

    override fun saveRefreshToken(token: String) {
        tokenLocalDataSource.saveRefreshToken(token)
    }

    override fun clearRefreshToken() {
        tokenLocalDataSource.clearAll()
    }
}
