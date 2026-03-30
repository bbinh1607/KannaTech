package com.example.shared.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRegisterRequest(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("email")
    val email: String,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("address")
    val address: String? = null,
)
