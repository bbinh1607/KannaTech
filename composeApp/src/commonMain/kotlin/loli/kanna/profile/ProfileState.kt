package loli.kanna.profile

import com.example.shared.domain.entity.UserEntity

data class ProfileState(
    val isLoading: Boolean = false,
    val user: UserEntity? = null,
    val error: String? = null
)
