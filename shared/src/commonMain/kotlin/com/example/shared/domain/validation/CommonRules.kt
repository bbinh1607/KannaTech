package com.example.shared.domain.validation

class SimpleRule(
    private val errorMessage: String,
    private val predicate: (String) -> Boolean,
) : ValidationRule {
    override fun validate(value: String): ValidationResult =
        if (predicate(value)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errorMessage)
        }
}

object NotEmptyRule : ValidationRule by SimpleRule(
    "Không được để trống",
    { it.isNotBlank() },
)

class MinLengthRule(
    private val min: Int,
) : ValidationRule by SimpleRule(
        "Phải có ít nhất $min ký tự",
        { it.length >= min },
    )

object HasUpperCaseRule : ValidationRule by SimpleRule(
    "Phải có ít nhất 1 chữ hoa",
    { value -> value.any { it.isUpperCase() } },
)

object HasDigitRule : ValidationRule by SimpleRule(
    "Phải có ít nhất 1 chữ số",
    { value -> value.any { it.isDigit() } },
)

class ContainsAtRule(
    char: String,
) : ValidationRule by SimpleRule(
        "Phải có $char",
        { it.contains(char) },
    )
