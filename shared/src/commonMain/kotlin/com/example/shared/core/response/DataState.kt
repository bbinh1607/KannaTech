package com.example.shared.core.response

import com.example.shared.core.error.AppError

sealed class DataState<out T> {
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val error: AppError) : DataState<Nothing>()
    object Loading : DataState<Nothing>()
}

val DataState.Error.message: String?
    get() = when (val e = error) {
        is AppError.Network -> e.message
        is AppError.Validation -> e.message
        is AppError.Mapper -> e.throwable.message
        is AppError.Unknown -> e.throwable?.message
    }
