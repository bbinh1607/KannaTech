package com.example.shared.domain.usecase.token

import com.example.shared.core.usecase.UseCase
import com.example.shared.domain.repository.local.TokenRepository

class ClearTokenUseCase(
    private val tokenRepository: TokenRepository,
) : UseCase<Unit, Unit>() {
    public override suspend fun execute(params: Unit) {
        tokenRepository.clearAccessToken()
    }
}
