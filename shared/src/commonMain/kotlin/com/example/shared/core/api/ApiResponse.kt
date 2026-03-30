package com.example.shared.core.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("status")
    val status: String? = null,
    @SerialName("status_code")
    val statusCode: Int? = null,
    @SerialName("success")
    val success: Boolean = true, 
    @SerialName("data")
    val data: T? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("details")
    val details: String? = null,
) {
    // Kiểm tra thành công dựa trên cả 'success' hoặc 'status == success'
    val isActuallySuccess: Boolean 
        get() = success || status == "success" || statusCode == 200

    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(success = true, data = data)
        fun <T> error(message: String, details: String? = null): ApiResponse<T> = 
            ApiResponse(success = false, data = null, message = message, details = details)
    }
}
