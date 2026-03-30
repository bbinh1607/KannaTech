package loli.kanna.devicemanagement.ui.component

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.response.DataState
import com.example.shared.core.response.message
import com.example.shared.data.model.device.ComponentResponse
import com.example.shared.data.model.device.DeviceResponse
import com.example.shared.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComponentState(
    val isLoading: Boolean = false,
    val components: List<ComponentResponse> = emptyList(),
    val devices: List<DeviceResponse> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

class ComponentViewModel(
    private val repository: DeviceRepository
) : ScreenModel {
    private val _state = MutableStateFlow(ComponentState())
    val state: StateFlow<ComponentState> = _state.asStateFlow()

    init {
        getComponents()
        getDevices()
    }

    fun getComponents() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.getComponents()) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            components = result.data.results,
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

    fun getDevices() {
        screenModelScope.launch {
            when (val result = repository.getAllDevices()) {
                is DataState.Success -> {
                    _state.update { it.copy(devices = result.data.results) }
                }
                else -> { /* Handle error if needed */ }
            }
        }
    }

    fun createComponent(name: String, description: String, deviceId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.createComponent(name, description, deviceId)) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Thêm linh kiện thành công",
                            error = null
                        )
                    }
                    getComponents()
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

    fun deleteComponent(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.deleteComponent(id)) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Xóa linh kiện thành công",
                            error = null
                        )
                    }
                    getComponents()
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
