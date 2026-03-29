package com.example.shared.domain.entity

data class UserEntity(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val address: String,
    val phone: String,
    val role: RoleEntity,
)

data class RoleEntity(
    val id: String,
    val name: String,
    val rank: Int,
    val description: String,
)
