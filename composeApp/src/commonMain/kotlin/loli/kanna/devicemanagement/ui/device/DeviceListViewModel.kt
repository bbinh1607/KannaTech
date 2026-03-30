package loli.kanna.devicemanagement.ui.device

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.response.DataState
import com.example.shared.core.response.message
import com.example.shared.data.model.device.DeviceResponse
import com.example.shared.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeviceListState(
    val isLoading: Boolean = false,
    val devices: List<DeviceResponse> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
    val currentCategoryId: String? = null,
)

class DeviceListViewModel(
    private val repository: DeviceRepository,
    private val categoryId: String,
) : ScreenModel {
    private val _state = MutableStateFlow(DeviceListState(currentCategoryId = categoryId))
    val state: StateFlow<DeviceListState> = _state.asStateFlow()

    init {
        getDevices()
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }

    fun getDevices(forceRefresh: Boolean = false) {
        // Nếu đã có dữ liệu và không ép refresh thì không load lại
        if (!forceRefresh && _state.value.devices.isNotEmpty()) return

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, devices = emptyList()) }
            when (val result = repository.getDevicesByCategory(categoryId)) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            devices = result.data.results,
                            error = null,
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.message,
                        )
                    }
                }

                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun createDevice(
        name: String,
        description: String,
    ) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.createDevice(name, description, categoryId)) {
                is DataState.Success -> {
                    val newList =
                        _state.value.devices.toMutableList().apply {
                            add(0, result.data)
                        }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            devices = newList,
                            successMessage = "Thêm thiết bị '${result.data.name}' thành công!",
                            error = null,
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }

                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun deleteDevice(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.deleteDevice(id)) {
                is DataState.Success -> {
                    val newList = _state.value.devices.filter { it.id != id }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            devices = newList,
                            successMessage = "Xóa thiết bị thành công!",
                            error = null,
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }

                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    override fun onDispose() {
        super.onDispose()
        // Giải phóng dữ liệu khi Screen bị tiêu hủy
        _state.update { DeviceListState() }
    }
}
