package com.example.shared.core.api

import com.example.shared.core.network.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse

abstract class BaseRemoteDataSource(
    protected val client: HttpClient,
) {
    // ===== CORE SAFE CALL =====
    protected suspend inline fun <reified T> safeCall(crossinline request: suspend () -> HttpResponse): NetworkResult<T> =
        try {
            val response = request()
            val statusCode = response.status.value

            if (statusCode in 200..299) {
                val apiResponse = response.body<ApiResponse<T>>()

                if (apiResponse.success && apiResponse.data != null) {
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(
                        message = apiResponse.message ?: "Unknown error",
                        code = statusCode,
                    )
                }
            } else {
                val errorBody =
                    runCatching {
                        response.body<ApiResponse<Nothing>>()
                    }.getOrNull()

                NetworkResult.Error(
                    message = errorBody?.message ?: "HTTP $statusCode error",
                    code = statusCode,
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.message ?: "Network error",
            )
        }

    // ===== PUBLIC METHODS =====

    protected suspend inline fun <reified T> get(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> =
        safeCall {
            client.get(url) { block() }
        }

    protected suspend inline fun <reified T> post(
        url: String,
        body: Any? = null,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> =
        safeCall {
            client.post(url) {
                body?.let { setBody(it) }
                block()
            }
        }

    protected suspend inline fun <reified T> put(
        url: String,
        body: Any? = null,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> =
        safeCall {
            client.put(url) {
                body?.let { setBody(it) }
                block()
            }
        }

    protected suspend inline fun <reified T> delete(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> =
        safeCall {
            client.delete(url) { block() }
        }
}
