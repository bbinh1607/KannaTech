package com.example.shared.data.dataSource.remote

import com.example.shared.core.api.ApiResponse
import com.example.shared.core.api.BaseRemoteDataSource
import com.example.shared.core.network.NetworkResult
import com.example.shared.data.model.auth.AuthLoginRequest
import com.example.shared.data.model.auth.AuthRegisterRequest
import com.example.shared.data.model.auth.AuthTokenResponse
import com.example.shared.data.model.user.UserResponse
import io.ktor.client.HttpClient

interface AuthRemoteDataSource {
    suspend fun register(request: AuthRegisterRequest): NetworkResult<UserResponse>

    suspend fun login(request: AuthLoginRequest): NetworkResult<AuthTokenResponse>
}

class AuthRemoteDataSourceImpl(
    client: HttpClient,
) : BaseRemoteDataSource(client),
    AuthRemoteDataSource {
    companion object {
        private const val AUTH_LOGIN = "identity/public/auth/login"
        private const val AUTH_REGISTER = "identity/public/auth/register"
    }

    override suspend fun login(request: AuthLoginRequest): NetworkResult<AuthTokenResponse> = post(AUTH_LOGIN, request)

    override suspend fun register(request: AuthRegisterRequest): NetworkResult<UserResponse> = post(AUTH_REGISTER, request)
}
