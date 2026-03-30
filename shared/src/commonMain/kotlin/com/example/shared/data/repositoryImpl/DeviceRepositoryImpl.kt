package com.example.shared.data.repositoryImpl

import com.example.shared.core.network.toDataState
import com.example.shared.core.response.DataState
import com.example.shared.data.dataSource.remote.DeviceRemoteDataSource
import com.example.shared.data.model.device.*
import com.example.shared.domain.repository.DeviceRepository

class DeviceRepositoryImpl(
    private val remote: DeviceRemoteDataSource
) : DeviceRepository {
    override suspend fun getCategories(): DataState<List<DeviceCategoryResponse>> =
        remote.getCategories().toDataState { it }

    override suspend fun createCategory(name: String, description: String): DataState<DeviceCategoryResponse> =
        remote.createCategory(DeviceCategoryRequest(name, description)).toDataState { it }

    override suspend fun deleteCategory(id: String): DataState<Unit> =
        remote.deleteCategory(id).toDataState { it }

    override suspend fun getDevicesByCategory(categoryId: String): DataState<DeviceListResponse> =
        remote.getDevicesByCategory(categoryId).toDataState { it }

    override suspend fun getAllDevices(): DataState<DeviceListResponse> =
        remote.getAllDevices().toDataState { it }

    override suspend fun createDevice(
        name: String,
        description: String,
        categoryId: String
    ): DataState<DeviceResponse> =
        remote.createDevice(DeviceCreateRequest(name, description, categoryId)).toDataState { it }

    override suspend fun deleteDevice(id: String): DataState<Unit> =
        remote.deleteDevice(id).toDataState { it }

    override suspend fun getDeviceDetails(deviceId: String): DataState<DeviceDetailListResponse> =
        remote.getDeviceDetails(deviceId).toDataState { it }

    override suspend fun getAllDeviceDetails(): DataState<DeviceDetailListResponse> =
        remote.getAllDeviceDetails().toDataState { it }

    override suspend fun createDeviceDetail(request: DeviceDetailCreateRequest): DataState<DeviceDetailResponse> =
        remote.createDeviceDetail(request).toDataState { it }

    override suspend fun getDeviceDetailsByComponentId(componentId: String): DataState<DeviceDetailListResponse> =
        remote.getDeviceDetailsByComponentId(componentId).toDataState { it }

    override suspend fun getComponentDetails(deviceDetailId: String): DataState<ComponentDetailListResponse> =
        remote.getComponentDetails(deviceDetailId).toDataState { it }

    override suspend fun getComponents(): DataState<ComponentListResponse> =
        remote.getComponents().toDataState { it }

    override suspend fun createComponent(
        name: String,
        description: String,
        deviceId: String
    ): DataState<ComponentResponse> =
        remote.createComponent(ComponentCreateRequest(name, description, deviceId)).toDataState { it }

    override suspend fun deleteComponent(id: String): DataState<Unit> =
        remote.deleteComponent(id).toDataState { it }

    override suspend fun getComponentDetailsByComponentId(componentId: String): DataState<ComponentDetailListResponse> =
        remote.getComponentDetailsByComponentId(componentId).toDataState { it }

    override suspend fun updateComponentDetail(
        id: String,
        request: ComponentDetailUpdateRequest
    ): DataState<ComponentDetailResponse> =
        remote.updateComponentDetail(id, request).toDataState { it }

    override suspend fun createComponentDetail(request: ComponentDetailCreateRequest): DataState<ComponentDetailResponse> =
        remote.createComponentDetail(request).toDataState { it }
}
