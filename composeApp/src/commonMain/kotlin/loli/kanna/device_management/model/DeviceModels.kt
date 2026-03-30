package loli.kanna.device_management.model

import androidx.compose.ui.graphics.vector.ImageVector

data class DeviceCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val deviceCount: Int
)

data class DeviceType(
    val id: String,
    val categoryId: String,
    val name: String,
    val modelCode: String,
    val image: String? = null
)

data class DeviceInstance(
    val id: String,
    val typeId: String,
    val name: String, // Ví dụ: Máy giặt 1
    val serialNumber: String,
    val status: DeviceStatus = DeviceStatus.ACTIVE
)

enum class DeviceStatus {
    ACTIVE, MAINTENANCE, ERROR, INACTIVE
}

data class ComponentType(
    val id: String,
    val name: String, // Ví dụ: Lồng giặt, Mô tơ
    val description: String
)

data class ComponentInstance(
    val id: String,
    val componentTypeId: String,
    val deviceInstanceId: String,
    val name: String, // Ví dụ: Lồng giặt A1
    val status: String
)
