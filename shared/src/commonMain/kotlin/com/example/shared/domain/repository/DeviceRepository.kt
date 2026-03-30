package com.example.shared.domain.repository

import com.example.shared.core.response.DataState
import com.example.shared.data.model.device.*

interface DeviceRepository {
    suspend fun getCategories(): DataState<List<DeviceCategoryResponse>>
    suspend fun createCategory(name: String, description: String): DataState<DeviceCategoryResponse>
    suspend fun deleteCategory(id: String): DataState<Unit>
    suspend fun getDevicesByCategory(categoryId: String): DataState<DeviceListResponse>
    
    // Thêm hàm lấy tất cả thiết bị
    suspend fun getAllDevices(): DataState<DeviceListResponse>
    
    // Thêm hàm tạo thiết bị
    suspend fun createDevice(name: String, description: String, categoryId: String): DataState<DeviceResponse>
    suspend fun deleteDevice(id: String): DataState<Unit>
    
    // Thêm hàm lấy chi tiết thiết bị
    suspend fun getDeviceDetails(deviceId: String): DataState<DeviceDetailListResponse>
    suspend fun getAllDeviceDetails(): DataState<DeviceDetailListResponse>
    suspend fun createDeviceDetail(request: DeviceDetailCreateRequest): DataState<DeviceDetailResponse>
    suspend fun getDeviceDetailsByComponentId(componentId: String): DataState<DeviceDetailListResponse>
    
    // Thêm hàm lấy chi tiết linh kiện
    suspend fun getComponentDetails(deviceDetailId: String): DataState<ComponentDetailListResponse>

    // Quản lý linh kiện (Component)
    suspend fun getComponents(): DataState<ComponentListResponse>
    suspend fun createComponent(name: String, description: String, deviceId: String): DataState<ComponentResponse>
    suspend fun deleteComponent(id: String): DataState<Unit>
    
    // Lấy chi tiết linh kiện theo componentId
    suspend fun getComponentDetailsByComponentId(componentId: String): DataState<ComponentDetailListResponse>

    // Cập nhật chi tiết linh kiện
    suspend fun updateComponentDetail(id: String, request: ComponentDetailUpdateRequest): DataState<ComponentDetailResponse>

    // Tạo mới chi tiết linh kiện
    suspend fun createComponentDetail(request: ComponentDetailCreateRequest): DataState<ComponentDetailResponse>
}
