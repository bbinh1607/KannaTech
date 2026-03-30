package loli.kanna.devicemanagement.ui.category

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.response.DataState
import com.example.shared.data.model.device.DeviceCategoryResponse
import com.example.shared.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryState(
    val isLoading: Boolean = false,
    val categories: List<DeviceCategoryResponse> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null,
)

class CategoryViewModel(
    private val repository: DeviceRepository,
) : ScreenModel {
    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    init {
        getCategories()
    }

    fun clearMessages() {
        _state.update { it.copy(error = null, successMessage = null) }
    }

    fun getCategories() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.getCategories()) {
                is DataState.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            categories = result.data,
                            error = null,
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error.toString()) }
                }

                else -> {}
            }
        }
    }

    fun createCategory(
        name: String,
        description: String,
    ) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.createCategory(name, description)) {
                is DataState.Success -> {
                    // Thêm trực tiếp vào đầu danh sách để UI cập nhật ngay lập tức
                    val newList =
                        _state.value.categories.toMutableList().apply {
                            add(0, result.data)
                        }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            categories = newList,
                            successMessage = "Thêm danh mục '${result.data.name}' thành công!",
                            error = null,
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.error.toString()) }
                }

                else -> {}
            }
        }
    }

    fun deleteCategory(id: String) {
        screenModelScope.launch {
            when (val result = repository.deleteCategory(id)) {
                is DataState.Success -> {
                    val newList = _state.value.categories.filter { it.id != id }
                    _state.update {
                        it.copy(
                            categories = newList,
                            successMessage = "Đã xóa danh mục thành công",
                        )
                    }
                }

                is DataState.Error -> {
                    _state.update { it.copy(error = result.error.toString()) }
                }

                else -> {}
            }
        }
    }
}
