package com.example.shared.core.network

import com.example.shared.core.error.AppError
import com.example.shared.core.response.DataState


inline fun <T, R> NetworkResult<T>.toDataState(transform: (T) -> R): DataState<R> =
    when (this) {
        is NetworkResult.Success -> {
            try {
                DataState.Success(transform(data))
            } catch (e: Exception) {
                DataState.Error(
                    AppError.Mapper(e),
                )
            }
        }

        is NetworkResult.Error -> {
            DataState.Error(
                AppError.Network(
                    message = message,
                    code = code,
                ),
            )
        }
    }
