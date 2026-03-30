package com.example.shared.core.api

import com.example.shared.core.network.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

abstract class BaseRemoteDataSource(
    protected val client: HttpClient,
) {
    // Sử dụng chung cấu hình Json
    protected val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // ===== CORE SAFE CALL =====
    protected suspend inline fun <reified T> safeCall(crossinline request: suspend () -> HttpResponse): NetworkResult<T> =
        try {
            val response = request()
            val statusCode = response.status.value
            val rawBody = response.bodyAsText()
            
            println("KannaDebug: Raw Response ($statusCode): $rawBody")

            // Parse json thô trước để kiểm tra cấu trúc
            val jsonElement = json.parseToJsonElement(rawBody)
            val jsonObject = jsonElement.jsonObject
            
            // Lấy các field cơ bản
            val status = jsonObject["status"]?.toString()?.replace("\"", "")
            val statusCodeFromApi = jsonObject["status_code"]?.toString()?.toIntOrNull()
            val dataElement = jsonObject["data"]

            val isSuccess = status == "success" || statusCode == 200 || statusCodeFromApi == 200

            if (isSuccess) {
                if (T::class == Unit::class) {
                    NetworkResult.Success(Unit as T)
                } else {
                    try {
                        val data = dataElement?.let { json.decodeFromJsonElement<T>(it) }
                        if (data != null) {
                            NetworkResult.Success(data)
                        } else {
                            NetworkResult.Error("Dữ liệu trống")
                        }
                    } catch (e: Exception) {
                        println("KannaDebug: Data Mapping Error: ${e.message}")
                        NetworkResult.Error("Lỗi cấu trúc dữ liệu: ${e.message}")
                    }
                }
            } else {
                val message = jsonObject["message"]?.toString()?.replace("\"", "") 
                    ?: jsonObject["details"]?.toString()?.replace("\"", "")
                    ?: "Lỗi hệ thống ($statusCode)"
                NetworkResult.Error(message, statusCodeFromApi ?: statusCode)
            }
        } catch (e: Exception) {
            println("KannaDebug: Network/Request Exception: ${e.message}")
            NetworkResult.Error(e.message ?: "Network error")
        }

    // ===== PUBLIC METHODS =====

    protected suspend inline fun <reified T> get(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): NetworkResult<T> =
        safeCall {
            client.get(url) {
                block()
            }
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
