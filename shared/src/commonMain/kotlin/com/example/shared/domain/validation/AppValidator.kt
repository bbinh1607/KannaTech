package com.example.shared.domain.validation

object AppValidator {
    val email = EmailValidator()
    val password = PasswordValidator()
    val username = UsernameValidator()
}
