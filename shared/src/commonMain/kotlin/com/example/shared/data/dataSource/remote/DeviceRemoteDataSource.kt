package com.example.shared.data.dataSource.remote

import com.example.shared.core.api.BaseRemoteDataSource
import com.example.shared.core.network.NetworkResult
import com.example.shared.data.model.device.*
import io.ktor.client.HttpClient

interface DeviceRemoteDataSource {
    suspend fun getCategories(): NetworkResult<List<DeviceCategoryResponse>>
    suspend fun createCategory(request: DeviceCategoryRequest): NetworkResult<DeviceCategoryResponse>
    suspend fun deleteCategory(id: String): NetworkResult<Unit>
    suspend fun getDevicesByCategory(categoryId: String): NetworkResult<DeviceListResponse>
    suspend fun getAllDevices(): NetworkResult<DeviceListResponse>
    suspend fun createDevice(request: DeviceCreateRequest): NetworkResult<DeviceResponse>
    suspend fun deleteDevice(id: String): NetworkResult<Unit>
    suspend fun getDeviceDetails(deviceId: String): NetworkResult<DeviceDetailListResponse>
    suspend fun getAllDeviceDetails(): NetworkResult<DeviceDetailListResponse>
    suspend fun createDeviceDetail(request: DeviceDetailCreateRequest): NetworkResult<DeviceDetailResponse>
    suspend fun getDeviceDetailsByComponentId(componentId: String): NetworkResult<DeviceDetailListResponse>
    suspend fun getComponentDetails(deviceDetailId: String): NetworkResult<ComponentDetailListResponse>
    
    // Quản lý linh kiện (Component)
    suspend fun getComponents(): NetworkResult<ComponentListResponse>
    suspend fun createComponent(request: ComponentCreateRequest): NetworkResult<ComponentResponse>
    suspend fun deleteComponent(id: String): NetworkResult<Unit>

    // Lấy chi tiết linh kiện theo componentId
    suspend fun getComponentDetailsByComponentId(componentId: String): NetworkResult<ComponentDetailListResponse>

    // Cập nhật chi tiết linh kiện
    suspend fun updateComponentDetail(id: String, request: ComponentDetailUpdateRequest): NetworkResult<ComponentDetailResponse>

    // Tạo mới chi tiết linh kiện
    suspend fun createComponentDetail(request: ComponentDetailCreateRequest): NetworkResult<ComponentDetailResponse>
}

class DeviceRemoteDataSourceImpl(client: HttpClient) : BaseRemoteDataSource(client), DeviceRemoteDataSource {
    override suspend fun getCategories(): NetworkResult<List<DeviceCategoryResponse>> = 
        get(url = "device/categories/get-all")

    override suspend fun createCategory(request: DeviceCategoryRequest): NetworkResult<DeviceCategoryResponse> = 
        post("device/categories/create", request)

    override suspend fun deleteCategory(id: String): NetworkResult<Unit> = 
        delete("device/categories/$id")

    override suspend fun getDevicesByCategory(categoryId: String): NetworkResult<DeviceListResponse> =
        get(url = "device/devices/get-all?category_id=$categoryId")

    override suspend fun getAllDevices(): NetworkResult<DeviceListResponse> =
        get(url = "device/devices/get-all")

    override suspend fun createDevice(request: DeviceCreateRequest): NetworkResult<DeviceResponse> =
        post(url = "device/devices/create", body = request)

    override suspend fun deleteDevice(id: String): NetworkResult<Unit> =
        delete(url = "device/devices/$id")

    override suspend fun getDeviceDetails(deviceId: String): NetworkResult<DeviceDetailListResponse> =
        get(url = "device/device-details/get-all?device_id=$deviceId")

    override suspend fun getAllDeviceDetails(): NetworkResult<DeviceDetailListResponse> =
        get(url = "device/device-details/get-all")

    override suspend fun createDeviceDetail(request: DeviceDetailCreateRequest): NetworkResult<DeviceDetailResponse> =
        post(url = "device/device-details/create", body = request)

    override suspend fun getDeviceDetailsByComponentId(componentId: String): NetworkResult<DeviceDetailListResponse> =
        get(url = "device/device-details/get-all?component_id=$componentId")

    override suspend fun getComponentDetails(deviceDetailId: String): NetworkResult<ComponentDetailListResponse> =
        get(url = "device/component-details/get-all?device_detail_id=$deviceDetailId")

    override suspend fun getComponents(): NetworkResult<ComponentListResponse> =
        get(url = "device/components/get-all")

    override suspend fun createComponent(request: ComponentCreateRequest): NetworkResult<ComponentResponse> =
        post(url = "device/components/create", body = request)

    override suspend fun deleteComponent(id: String): NetworkResult<Unit> =
        delete(url = "device/components/$id")

    override suspend fun getComponentDetailsByComponentId(componentId: String): NetworkResult<ComponentDetailListResponse> =
        get(url = "device/component-details/get-all?component_id=$componentId")

    override suspend fun updateComponentDetail(id: String, request: ComponentDetailUpdateRequest): NetworkResult<ComponentDetailResponse> =
        put(url = "device/component-details/$id", body = request)

    override suspend fun createComponentDetail(request: ComponentDetailCreateRequest): NetworkResult<ComponentDetailResponse> =
        post(url = "device/component-details", body = request)
}
