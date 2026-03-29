package com.example.shared.data.dataSource.local

import com.russhwolf.settings.Settings

class TokenLocalDataSourceImpl(
    private val settings: Settings,
) : TokenLocalDataSource {
    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
    }

    override fun saveAccessToken(token: String?) {
        if (token == null) {
            settings.remove(ACCESS_TOKEN_KEY)
        } else {
            settings.putString(ACCESS_TOKEN_KEY, token)
        }
    }

    override fun getAccessToken(): String? = settings.getStringOrNull(ACCESS_TOKEN_KEY)

    override fun saveRefreshToken(token: String?) {
        if (token == null) {
            settings.remove(REFRESH_TOKEN_KEY)
        } else {
            settings.putString(REFRESH_TOKEN_KEY, token)
        }
    }

    override fun getRefreshToken(): String? = settings.getStringOrNull(REFRESH_TOKEN_KEY)

    override fun clearAll() {
        settings.remove(ACCESS_TOKEN_KEY)
        settings.remove(REFRESH_TOKEN_KEY)
        // Xóa sạch hoàn toàn
        settings.clear()
    }
}
