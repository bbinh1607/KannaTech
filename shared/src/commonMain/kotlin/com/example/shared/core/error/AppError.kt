package com.example.shared.core.error

sealed class AppError {
    data class Network(
        val message: String?,
        val code: Int?,
    ) : AppError()

    data class Validation(
        val field: String,
        val message: String,
    ) : AppError()

    data class Mapper(
        val throwable: Throwable,
    ) : AppError()

    data class Unknown(
        val throwable: Throwable?,
    ) : AppError()
}
