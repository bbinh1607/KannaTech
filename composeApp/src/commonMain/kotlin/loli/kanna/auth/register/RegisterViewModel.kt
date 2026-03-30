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
            is RegisterAction.UsernameChanged -> {
                _state.update { it.copy(username = action.username, usernameError = null) }
            }

            is RegisterAction.EmailChanged -> {
                _state.update { it.copy(email = action.email, emailError = null) }
            }

            is RegisterAction.PasswordChanged -> {
                _state.update { it.copy(password = action.password, passwordError = null) }
            }

            is RegisterAction.ImageUrlChanged -> {
                _state.update { it.copy(imageUrl = action.imageUrl) }
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
            }
        }
    }

    private fun register() {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    registerError = null,
                    usernameError = null,
                    emailError = null,
                    passwordError = null,
                    phoneError = null,
                    addressError = null,
                )
            }

            val params =
                RegisterParams(
                    username = _state.value.username,
                    email = _state.value.email,
                    password = _state.value.password,
                    imageUrl = _state.value.imageUrl.ifBlank { null },
                    address = _state.value.address.ifBlank { null },
                    phone = _state.value.phone.ifBlank { null },
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
                            handleValidationError(error)
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

    private fun handleValidationError(error: AppError.Validation) {
        when (error.field) {
            "username" -> _state.update { it.copy(usernameError = error.message) }
            "email" -> _state.update { it.copy(emailError = error.message) }
            "password" -> _state.update { it.copy(passwordError = error.message) }
            else -> _state.update { it.copy(registerError = error.message) }
        }
    }
}
