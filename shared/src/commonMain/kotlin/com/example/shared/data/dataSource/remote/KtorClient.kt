package com.example.shared.data.dataSource.remote

import com.example.shared.data.dataSource.local.TokenLocalDataSource
import com.example.shared.getBaseUrlApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// 👇 expect engine
expect fun provideHttpClientEngine(): io.ktor.client.engine.HttpClientEngine

class KtorClient(
    private val tokenLocalDataSource: TokenLocalDataSource,
) {
    private val jsonConfig =
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

    val httpClient: HttpClient by lazy {
        HttpClient(provideHttpClientEngine()) {

            expectSuccess = false

            install(ContentNegotiation) {
                json(jsonConfig)
            }

            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.BODY
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = tokenLocalDataSource.getAccessToken()
                        val refreshToken = tokenLocalDataSource.getRefreshToken()
                        
                        if (accessToken != null) {
                            BearerTokens(accessToken, refreshToken ?: "")
                        } else {
                            null
                        }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000 // Tăng lên 30 giây
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            defaultRequest {
                url {
                    takeFrom(getBaseUrlApi())
                }
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}
