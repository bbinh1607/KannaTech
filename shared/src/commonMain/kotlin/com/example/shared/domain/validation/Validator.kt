package com.example.shared.domain.validation

abstract class BaseValidator(
    private val rules: List<ValidationRule>,
) {
    fun validate(value: String): ValidationResult =
        rules
            .asSequence()
            .map { it.validate(value) }
            .firstOrNull { it is ValidationResult.Error }
            ?: ValidationResult.Success
}

class PasswordValidator :
    BaseValidator(
        listOf(
            NotEmptyRule,
            MinLengthRule(6),
        ),
    ) {
    operator fun invoke(value: String) = validate(value)
}

class EmailValidator :
    BaseValidator(
        listOf(
            NotEmptyRule,
            ContainsAtRule("@"),
        ),
    ) {
    operator fun invoke(value: String) = validate(value)
}

class UsernameValidator :
    BaseValidator(
        listOf(
            NotEmptyRule,
            MinLengthRule(3),
        ),
    ) {
    operator fun invoke(value: String) = validate(value)
}

class NameValidator :
    BaseValidator(
        listOf(
            NotEmptyRule,
        ),
    ) {
    operator fun invoke(value: String) = validate(value)
}
