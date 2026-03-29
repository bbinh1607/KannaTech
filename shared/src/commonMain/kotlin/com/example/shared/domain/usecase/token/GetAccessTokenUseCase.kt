package com.example.shared.domain.usecase.token

import com.example.shared.core.usecase.UseCase
import com.example.shared.domain.repository.local.TokenRepository

class GetAccessTokenUseCase(
    private val tokenRepository: TokenRepository,
) : UseCase<Unit, String?>() {
    public override suspend fun execute(params: Unit): String? = tokenRepository.getAccessToken()
}
