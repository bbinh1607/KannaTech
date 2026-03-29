package com.example.shared.data.repositoryImpl


import com.example.shared.core.network.toDataState
import com.example.shared.core.response.DataState
import com.example.shared.data.dataSource.local.TokenLocalDataSource
import com.example.shared.data.dataSource.remote.AuthRemoteDataSource
import com.example.shared.data.mapper.AuthMapper
import com.example.shared.domain.entity.TokenEntity
import com.example.shared.domain.entity.UserEntity
import com.example.shared.domain.param.LoginParams
import com.example.shared.domain.param.RegisterParams
import com.example.shared.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val remote: AuthRemoteDataSource,
    private val local: TokenLocalDataSource,
    private val mapper: AuthMapper,
) : AuthRepository {
    override suspend fun register(params: RegisterParams): DataState<UserEntity> =
        remote
            .register(mapper.fromRegisterParams(params))
            .toDataState { response ->
                mapper.toUserEntity(response)
            }

    override suspend fun login(params: LoginParams): DataState<TokenEntity> =
        remote
            .login(mapper.fromLoginParams(params))
            .toDataState { response ->
                val entity = mapper.toTokenEntity(response)
                local.saveAccessToken(entity.accessToken)
                local.saveRefreshToken(entity.refreshToken)
                entity
            }
}
