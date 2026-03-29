package loli.kanna.auth.register

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.error.AppError
import com.example.shared.core.response.DataState
import com.example.shared.domain.param.RegisterParams
import com.example.shared.domain.usecase.auth.AuthRegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRegisterUseCase: AuthRegisterUseCase,
) : ScreenModel {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.NameChanged -> {
                _state.update { it.copy(name = action.name, nameError = null) }
            }

            is RegisterAction.EmailChanged -> {
                _state.update { it.copy(email = action.email, emailError = null) }
            }

            is RegisterAction.PasswordChanged -> {
                _state.update { it.copy(password = action.password, passwordError = null) }
            }

            is RegisterAction.AddressChanged -> {
                _state.update { it.copy(address = action.address, addressError = null) }
            }

            is RegisterAction.PhoneChanged -> {
                _state.update { it.copy(phone = action.phone, phoneError = null) }
            }

            RegisterAction.OnRegisterClick -> {
                register()
            }

            RegisterAction.OnBackClick -> {
                // Thường xử lý ở UI
            }
        }
    }

    private fun register() {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    registerError = null,
                    nameError = null,
                    emailError = null,
                    passwordError = null,
                    phoneError = null,
                    addressError = null,
                )
            }

            val params =
                RegisterParams(
                    name = _state.value.name,
                    email = _state.value.email,
                    password = _state.value.password,
                    address = _state.value.address,
                    phone = _state.value.phone,
                )

            when (val result = authRegisterUseCase.execute(params)) {
                is DataState.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }

                is DataState.Error -> {
                    val error = result.error
                    _state.update { it.copy(isLoading = false) }

                    when (error) {
                        is AppError.Validation -> {
                            _state.update { it.copy(registerError = error.message) }
                        }

                        is AppError.Network -> {
                            _state.update { it.copy(registerError = error.message) }
                        }

                        else -> {
                            _state.update { it.copy(registerError = "Đã có lỗi xảy ra") }
                        }
                    }
                }

                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }
}
