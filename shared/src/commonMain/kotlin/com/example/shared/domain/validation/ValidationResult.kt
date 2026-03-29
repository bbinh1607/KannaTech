package com.example.shared.domain.validation

sealed class ValidationResult {
    object Success : ValidationResult()

    data class Error(
        val message: String,
    ) : ValidationResult()
}
