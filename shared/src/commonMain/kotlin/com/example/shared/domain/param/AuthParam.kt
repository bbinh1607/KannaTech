package com.example.shared.domain.param

data class RegisterParams(
    val name: String,
    val email: String,
    val password: String,
    val address: String?,
    val phone: String?,
)

data class LoginParams(
    val username: String,
    val password: String,
)