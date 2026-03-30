package com.example.shared.data.mapper

import com.example.shared.core.mapper.BaseMapper
import com.example.shared.data.model.user.UserResponse
import com.example.shared.domain.entity.RoleEntity
import com.example.shared.domain.entity.UserEntity

class UserMapper : BaseMapper<UserResponse, UserEntity> {
    override fun toEntity(response: UserResponse): UserEntity =
        UserEntity(
            id = response.id ?: "",
            name = response.username, // Đổi từ name sang username theo UserResponse mới
            email = response.email ?: "",
            password = response.password,
            address = response.address ?: "",
            phone = response.phone ?: "",
            role =
                RoleEntity(
                    id = response.role?.id ?: "",
                    name = response.role?.name ?: "guest",
                    rank = response.role?.rank ?: 0,
                    description = response.role?.description ?: "",
                ),
        )
}
