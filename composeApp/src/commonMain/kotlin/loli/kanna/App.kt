package loli.kanna

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.example.shared.domain.usecase.token.GetAccessTokenUseCase
import loli.kanna.auth.login.LoginScreen
import loli.kanna.jobtracker.presentation.habit.home.HabitHomeScreen
import org.koin.compose.koinInject

@Composable
fun App() {
    val getAccessTokenUseCase = koinInject<GetAccessTokenUseCase>()
    var startScreen by remember { mutableStateOf<Screen?>(null) }

    LaunchedEffect(Unit) {
        startScreen = HabitHomeScreen()
//        val token = getAccessTokenUseCase.execute(Unit)
//        if (token != null && token.isNotEmpty()) {
//            startScreen = HabitScreen()
//        } else {
//            startScreen = LoginScreen()
//        }
    }

    MaterialTheme {
        startScreen?.let { screen ->
            Navigator(screen)
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
