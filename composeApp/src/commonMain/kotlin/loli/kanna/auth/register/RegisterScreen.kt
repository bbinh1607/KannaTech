package loli.kanna.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import loli.kanna.auth.register.components.RegisterContent

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<RegisterViewModel>()
        val state by screenModel.state.collectAsState()

        // Handle successful registration
        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) {
                navigator.pop() // Return to LoginScreen
            }
        }

        RegisterContent(
            state = state,
            onAction = { action ->
                if (action is RegisterAction.OnBackClick) {
                    navigator.pop()
                } else {
                    screenModel.onAction(action)
                }
            },
            onBackClick = { navigator.pop() }
        )
    }
}
