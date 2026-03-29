package com.example.shared.core.response

import com.example.shared.core.error.AppError

sealed class DataState<out T> {
    data class Success<T>(
        val data: T,
    ) : DataState<T>()

    data class Error(
        val error: AppError,
    ) : DataState<Nothing>()

    data object Loading : DataState<Nothing>()
}
