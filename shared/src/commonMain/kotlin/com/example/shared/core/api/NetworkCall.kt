package com.example.shared.core.api

import com.example.shared.core.network.NetworkResult

suspend fun <T> safeApiCall(apiCall: suspend () -> ApiResponse<T>): NetworkResult<T> =
    try {
        val response = apiCall()

        if (response.success && response.data != null) {
            NetworkResult.Success(response.data)
        } else {
            NetworkResult.Error(response.message ?: "Unknown Server Error")
        }
    } catch (e: Exception) {
        // Handles No Internet, Timeout, Server Down (500), etc.
        NetworkResult.Error(e.message ?: "Network Request Failed")
    }
