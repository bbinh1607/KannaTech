package loli.kanna.auth.login

import kotlinx.serialization.Serializable

/**
 * Object used for a type safe destination to a Login route
 */
@Serializable
object LoginDestination

/**
 * UI State that represents LoginScreen
 **/
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
    val accessToken: String? = null
)

/**
 * Login Actions emitted from the UI Layer
 * passed to the coordinator to handle
 **/
sealed interface LoginAction {
    data class EmailChanged(val email: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data object OnLoginClick : LoginAction
    data object OnRegisterClick : LoginAction
    data object GetToken : LoginAction
}
