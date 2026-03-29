package com.example.shared.domain.usecase.auth

import com.example.shared.core.response.DataState
import com.example.shared.core.usecase.UseCase
import com.example.shared.domain.entity.TokenEntity
import com.example.shared.domain.param.LoginParams
import com.example.shared.domain.repository.AuthRepository
import com.example.shared.domain.validation.AppValidator
import com.example.shared.domain.validation.validateFirstError

class AuthLoginUseCase(
    private val repository: AuthRepository,
) : UseCase<LoginParams, DataState<TokenEntity>>() {
    public override suspend fun execute(params: LoginParams): DataState<TokenEntity> {
//        validateFirstError(
//            AppValidator.email(params.username) to LoginParams::username,
//            AppValidator.password(params.password) to LoginParams::password,
//        )?.let {
//            return DataState.Error(it)
//        }

        return repository.login(params)
    }
}
