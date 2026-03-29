package loli.kanna.auth.login

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.core.error.AppError
import com.example.shared.core.response.DataState
import com.example.shared.domain.param.LoginParams
import com.example.shared.domain.usecase.auth.AuthLoginUseCase
import com.example.shared.domain.usecase.token.GetAccessTokenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authLoginUseCase: AuthLoginUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase
) : ScreenModel {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> {
                _state.update { it.copy(email = action.email, emailError = null) }
            }

            is LoginAction.PasswordChanged -> {
                _state.update { it.copy(password = action.password, passwordError = null) }
            }

            LoginAction.OnLoginClick -> {
                login()
            }

            LoginAction.OnRegisterClick -> {
                // Handle navigation to register if needed
            }

            LoginAction.GetToken -> {
                getToken()
            }
        }
    }

    private fun getToken() {
        screenModelScope.launch {
            val token = getAccessTokenUseCase.execute(Unit)
            _state.update { it.copy(accessToken = token) }
        }
    }

    private fun login() {
        screenModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    loginError = null,
                    emailError = null,
                    passwordError = null,
                )
            }

            val params =
                LoginParams(
                    username = _state.value.email,
                    password = _state.value.password,
                )

            when (val result = authLoginUseCase.execute(params)) {
                is DataState.Success -> {
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            isSuccess = true,
                            accessToken = result.data.accessToken
                        ) 
                    }
                }

                is DataState.Error -> {
                    val error = result.error
                    when (error) {
                        is AppError.Validation -> {
                            _state.update { state ->
                                state.copy(
                                    isLoading = false,
                                    loginError = error.message,
                                )
                            }
                        }

                        is AppError.Network -> {
                            _state.update { it.copy(isLoading = false, loginError = error.message) }
                        }

                        else -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    loginError = "An unknown error occurred",
                                )
                            }
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
