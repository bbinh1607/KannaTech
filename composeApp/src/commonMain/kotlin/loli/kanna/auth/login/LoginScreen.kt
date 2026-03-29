package loli.kanna.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import loli.kanna.auth.register.RegisterScreen
import loli.kanna.jobtracker.presentation.habit.home.HabitHomeScreen

class LoginScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LoginViewModel>()
        val state by screenModel.state.collectAsState()

        var passwordVisible by remember { mutableStateOf(false) }

        // LẮNG NGHE SỰ KIỆN ĐĂNG NHẬP THÀNH CÔNG
        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) {
                navigator.replaceAll(HabitHomeScreen())
            }
        }

        Scaffold { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Chào mừng quay lại!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = "Đăng nhập để tiếp tục",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 32.dp),
                    )

                    // EMAIL
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = {
                            screenModel.onAction(LoginAction.EmailChanged(it))
                        },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null)
                        },
                        isError = state.emailError != null,
                        supportingText = {
                            state.emailError?.let { Text(it) }
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                            ),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PASSWORD
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = {
                            screenModel.onAction(LoginAction.PasswordChanged(it))
                        },
                        label = { Text("Mật khẩu") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                passwordVisible = !passwordVisible
                            }) {
                                Icon(
                                    imageVector =
                                        if (passwordVisible) {
                                            Icons.Filled.Visibility
                                        } else {
                                            Icons.Filled.VisibilityOff
                                        },
                                    contentDescription = null,
                                )
                            }
                        },
                        visualTransformation =
                            if (passwordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                        isError = state.passwordError != null,
                        supportingText = {
                            state.passwordError?.let { Text(it) }
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                            ),
                        singleLine = true,
                    )

                    // ERROR
                    if (state.loginError != null) {
                        Text(
                            text = state.loginError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // BUTTON
                    Button(
                        onClick = {
                            screenModel.onAction(LoginAction.OnLoginClick)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        enabled = !state.isLoading,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text(
                                "Đăng nhập",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = {
                            navigator.push(RegisterScreen())
                        },
                    ) {
                        Text("Chưa có tài khoản? Đăng ký ngay")
                    }

                    // HIỂN THỊ ACCESS TOKEN ĐỂ KIỂM TRA
                    Spacer(modifier = Modifier.height(32.dp))
                    @Suppress("DEPRECATION")
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { screenModel.onAction(LoginAction.GetToken) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Check Saved Token")
                    }

                    if (state.accessToken != null) {
                        Text(
                            text = "Token trong LocalStorage:",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        androidx.compose.foundation.text.selection.SelectionContainer {
                            Text(
                                text = state.accessToken!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
