package loli.kanna.auth.register

import kotlinx.serialization.Serializable

@Serializable
object RegisterDestination

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val address: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val phoneError: String? = null,
    val addressError: String? = null,
    val registerError: String? = null,
    val isSuccess: Boolean = false
)

sealed interface RegisterAction {
    data class NameChanged(val name: String) : RegisterAction
    data class EmailChanged(val email: String) : RegisterAction
    data class PasswordChanged(val password: String) : RegisterAction
    data class AddressChanged(val address: String) : RegisterAction
    data class PhoneChanged(val phone: String) : RegisterAction
    data object OnRegisterClick : RegisterAction
    data object OnBackClick : RegisterAction
}
