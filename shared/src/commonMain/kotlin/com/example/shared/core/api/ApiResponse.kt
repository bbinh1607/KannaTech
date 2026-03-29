package com.example.shared.core.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean = true, // Gán mặc định là true để tránh lỗi khi Server không gửi
    val data: T? = null,
    val message: String? = null,
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(success = true, data = data)

        fun <T> error(message: String): ApiResponse<T> = ApiResponse(success = false, data = null, message = message)
    }
}
