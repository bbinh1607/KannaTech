package com.example.shared.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("pass")
    val password: String,
    @SerialName("address")
    val address: String? = null,
    @SerialName("phone")
    val phone: String? = null,
)
