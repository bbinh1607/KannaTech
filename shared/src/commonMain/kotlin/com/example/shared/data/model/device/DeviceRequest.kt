package com.example.shared.data.model.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceCreateRequest(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("category_id")
    val categoryId: String
)

@Serializable
data class DeviceDetailCreateRequest(
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("area")
    val area: String
)

@Serializable
data class ComponentCreateRequest(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("device_id")
    val deviceId: String
)

@Serializable
data class ComponentDetailCreateRequest(
    @SerialName("component_id")
    val componentId: String,
    @SerialName("device_detail_id")
    val deviceDetailId: String? = null,
    @SerialName("buy_at")
    val buyAt: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("expirationDate")
    val expirationDate: String? = null,
    @SerialName("price")
    val price: Double
)

@Serializable
data class ComponentDetailUpdateRequest(
    @SerialName("component_id")
    val componentId: String? = null,
    @SerialName("device_detail_id")
    val deviceDetailId: String? = null,
    @SerialName("buy_at")
    val buyAt: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("expirationDate")
    val expirationDate: String? = null,
    @SerialName("price")
    val price: Double? = null
)
