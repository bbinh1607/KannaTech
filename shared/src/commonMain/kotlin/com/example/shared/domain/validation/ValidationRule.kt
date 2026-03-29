package com.example.shared.domain.validation

fun interface ValidationRule {
    fun validate(value: String): ValidationResult
}
