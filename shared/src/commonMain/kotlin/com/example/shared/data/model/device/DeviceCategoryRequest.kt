package com.example.shared.data.model.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCategoryRequest(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String
)
