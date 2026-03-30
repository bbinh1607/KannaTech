package loli.kanna.devicemanagement.ui.device

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.response.DataState
import com.example.shared.core.response.message
import com.example.shared.data.model.device.DeviceDetailCreateRequest
import com.example.shared.data.model.device.DeviceDetailResponse
import com.example.shared.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceDetailState(
    val isLoading: Boolean = false,
    val details: List<DeviceDetailResponse> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class DeviceDetailViewModel(
    private val repository: DeviceRepository,
    private val deviceId: String
) : ScreenModel {
    private val _state = MutableStateFlow(DeviceDetailState())
    val state: StateFlow<DeviceDetailState> = _state.asStateFlow()

    init {
        getDetails()
    }

    fun getDetails() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.getDeviceDetails(deviceId)) {
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

    fun createDeviceDetail(area: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val request = DeviceDetailCreateRequest(deviceId = deviceId, area = area)
            when (val result = repository.createDeviceDetail(request)) {
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
