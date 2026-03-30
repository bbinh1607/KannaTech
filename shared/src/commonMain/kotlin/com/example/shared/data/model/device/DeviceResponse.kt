package com.example.shared.data.model.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceListResponse(
    @SerialName("results")
    val results: List<DeviceResponse>,
    @SerialName("total")
    val total: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class DeviceResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("category")
    val category: DeviceCategoryResponse? = null,
    @SerialName("list_component")
    val listComponent: List<ComponentResponse> = emptyList(),
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class ComponentResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("device_id")
    val deviceId: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("warrantyMonth")
    val warrantyMonth: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class ComponentListResponse(
    @SerialName("results")
    val results: List<ComponentResponse>,
    @SerialName("total")
    val total: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class DeviceDetailResponse(
    @SerialName("id")
    val id: String,
    @SerialName("area")
    val area: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("buyAt")
    val buyAt: String? = null,
    @SerialName("warranty")
    val warranty: String? = null,
    @SerialName("device")
    val device: DeviceResponse? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class DeviceDetailListResponse(
    @SerialName("results")
    val results: List<DeviceDetailResponse>,
    @SerialName("total")
    val total: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("total_pages")
    val totalPages: Int
)

@Serializable
data class ComponentDetailResponse(
    @SerialName("id")
    val id: String,
    @SerialName("status")
    val status: String? = null,
    @SerialName("price")
    val price: Double? = null,
    @SerialName("buy_at")
    val buyAt: String? = null,
    @SerialName("expirationDate")
    val expirationDate: String? = null,
    @SerialName("component")
    val component: ComponentResponse? = null,
    @SerialName("device_detail")
    val deviceDetail: DeviceDetailResponse? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class ComponentDetailListResponse(
    @SerialName("results")
    val results: List<ComponentDetailResponse>,
    @SerialName("total")
    val total: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("limit")
    val limit: Int,
    @SerialName("total_pages")
    val totalPages: Int
)
