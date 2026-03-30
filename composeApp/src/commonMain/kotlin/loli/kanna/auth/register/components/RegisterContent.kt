package loli.kanna.auth.register.components

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
import loli.kanna.auth.register.RegisterAction
import loli.kanna.auth.register.RegisterState

@Composable
fun RegisterContent(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit,
    onBackClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Tạo tài khoản!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "Đăng ký để bắt đầu hành trình của bạn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 32.dp),
                )

                // USERNAME
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onAction(RegisterAction.EmailChanged(it)) },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    isError = state.emailError != null,
                    supportingText = { state.emailError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.username,
                    onValueChange = { onAction(RegisterAction.UsernameChanged(it)) },
                    label = { Text("Tên đăng nhập") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    isError = state.usernameError != null,
                    supportingText = { state.usernameError?.let { Text(it) } },
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onAction(RegisterAction.PasswordChanged(it)) },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = state.passwordError != null,
                    supportingText = { state.passwordError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))

                // PHONE
                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { onAction(RegisterAction.PhoneChanged(it)) },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    isError = state.phoneError != null,
                    supportingText = { state.phoneError?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ADDRESS
                OutlinedTextField(
                    value = state.address,
                    onValueChange = { onAction(RegisterAction.AddressChanged(it)) },
                    label = { Text("Địa chỉ") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    isError = state.addressError != null,
                    supportingText = { state.addressError?.let { Text(it) } },
                    singleLine = true,
                )

//                Spacer(modifier = Modifier.height(16.dp))
//
//                // IMAGE URL (UUID)
//                OutlinedTextField(
//                    value = state.imageUrl,
//                    onValueChange = { onAction(RegisterAction.ImageUrlChanged(it)) },
//                    label = { Text("ID ảnh (UUID)") },
//                    modifier = Modifier.fillMaxWidth(),
//                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
//                    singleLine = true,
//                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD


                // ERROR
                if (state.registerError != null) {
                    Text(
                        text = state.registerError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BUTTON
                Button(
                    onClick = { onAction(RegisterAction.OnRegisterClick) },
                    modifier = Modifier
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
                            "Đăng ký",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBackClick) {
                    Text("Đã có tài khoản? Đăng nhập ngay")
                }
            }
        }
    }
}
