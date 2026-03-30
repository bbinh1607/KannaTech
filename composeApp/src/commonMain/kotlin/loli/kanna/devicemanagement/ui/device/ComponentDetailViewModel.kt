package loli.kanna.devicemanagement.ui.device

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.response.DataState
import com.example.shared.core.response.message
import com.example.shared.data.model.device.ComponentDetailCreateRequest
import com.example.shared.data.model.device.ComponentDetailResponse
import com.example.shared.data.model.device.ComponentDetailUpdateRequest
import com.example.shared.data.model.device.DeviceDetailResponse
import com.example.shared.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComponentDetailState(
    val isLoading: Boolean = false,
    val details: List<ComponentDetailResponse> = emptyList(),
    val deviceDetails: List<DeviceDetailResponse> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class ComponentDetailViewModel(
    private val repository: DeviceRepository,
    private val id: String,
    private val isFromComponent: Boolean = false
) : ScreenModel {
    private val _state = MutableStateFlow(ComponentDetailState())
    val state: StateFlow<ComponentDetailState> = _state.asStateFlow()

    init {
        getDetails()
        if (isFromComponent) {
            getAllDeviceDetails()
        }
    }

    fun getDetails() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (isFromComponent) {
                repository.getComponentDetailsByComponentId(id)
            } else {
                repository.getComponentDetails(id)
            }
            
            when (result) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            details = result.data.results,
                            error = null
                        )
                    }
                }
                is DataState.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun getAllDeviceDetails() {
        screenModelScope.launch {
            // Sử dụng hàm getDeviceDetailsByComponentId để gọi API device-details/get-all?component_id=...
            when (val result = repository.getAllDeviceDetails()) {
                is DataState.Success -> {
                    _state.update { it.copy(deviceDetails = result.data.results) }
                }
                else -> {}
            }
        }
    }

    fun updateComponentDetail(id: String, request: ComponentDetailUpdateRequest) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.updateComponentDetail(id, request)) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Cập nhật thành công",
                            error = null
                        )
                    }
                    getDetails()
                }
                is DataState.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun createComponentDetail(request: ComponentDetailCreateRequest) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.createComponentDetail(request)) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Tạo mới thành công",
                            error = null
                        )
                    }
                    getDetails()
                }
                is DataState.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }
}
