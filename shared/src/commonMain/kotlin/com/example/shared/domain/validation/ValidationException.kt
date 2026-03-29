package com.example.shared.domain.validation

import com.example.shared.core.error.AppError
import com.example.shared.core.response.DataState
import kotlin.reflect.KProperty1

fun <T> ValidationResult.validateOrError(property: KProperty1<T, *>): DataState.Error? =
    if (this is ValidationResult.Error) {
        DataState.Error(
            AppError.Validation(
                field = property.name,
                message = message,
            ),
        )
    } else {
        null
    }

fun <T> ValidationResult.toValidationError(property: KProperty1<T, *>): AppError.Validation? =
    if (this is ValidationResult.Error) {
        AppError.Validation(property.name, message)
    } else {
        null
    }

fun <T> validateFirstError(vararg validations: Pair<ValidationResult, KProperty1<T, *>>): AppError.Validation? {
    validations.forEach { (result, property) ->
        result.toValidationError(property)?.let { return it }
    }
    return null
}
