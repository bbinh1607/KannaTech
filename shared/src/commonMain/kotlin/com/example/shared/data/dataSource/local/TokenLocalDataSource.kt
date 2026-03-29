package com.example.shared.data.dataSource.local

interface TokenLocalDataSource {
    fun saveAccessToken(token: String?)
    fun getAccessToken(): String?
    fun saveRefreshToken(token: String?)
    fun getRefreshToken(): String?
    fun clearAll()
}
