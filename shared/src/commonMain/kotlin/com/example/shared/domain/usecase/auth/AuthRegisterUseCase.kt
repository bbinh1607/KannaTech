package com.example.shared.domain.usecase.auth

import com.example.shared.core.response.DataState
import com.example.shared.core.usecase.UseCase
import com.example.shared.domain.entity.UserEntity
import com.example.shared.domain.param.RegisterParams
import com.example.shared.domain.repository.AuthRepository
import com.example.shared.domain.validation.AppValidator
import com.example.shared.domain.validation.validateFirstError

class AuthRegisterUseCase(
    private val repository: AuthRepository,
) : UseCase<RegisterParams, DataState<UserEntity>>() {
    public override suspend fun execute(params: RegisterParams): DataState<UserEntity> {
        validateFirstError<RegisterParams>(
            AppValidator.username(params.username) to RegisterParams::username,
            AppValidator.email(params.email) to RegisterParams::email,
            AppValidator.password(params.password) to RegisterParams::password,
        )?.let {
            return DataState.Error(it)
        }

        return repository.register(params)
    }
}
