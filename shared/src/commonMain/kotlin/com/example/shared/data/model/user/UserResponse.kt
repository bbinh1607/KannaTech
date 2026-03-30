package com.example.shared.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("id")
    val id: String? = null,
    @SerialName("username")
    val username: String, // Không null theo yêu cầu
    @SerialName("email")
    val email: String? = null,
    @SerialName("password")
    val password: String, // Không null theo yêu cầu
    @SerialName("address")
    val address: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("role")
    val role: RoleResponse? = null,
    @SerialName("is_active")
    val isActive: Boolean? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class RoleResponse(
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("rank")
    val rank: Int? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)
