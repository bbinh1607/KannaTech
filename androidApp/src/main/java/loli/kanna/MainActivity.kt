package loli.kanna

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Start Foreground Service
        val serviceIntent = Intent(this, LockScreenService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)

        setContent {
            var hasOverlayPermission by remember {
                mutableStateOf(Settings.canDrawOverlays(this))
            }

            var hasNotificationPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS,
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    },
                )
            }

            var hasExactAlarmPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.canScheduleExactAlarms()
                    } else {
                        true
                    },
                )
            }

            val permissionLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { isGranted ->
                    hasNotificationPermission = isGranted
                    Log.d("MainActivity", "Notification permission granted: $isGranted")
                }

            // Tự động xin quyền thông báo khi vào app
            LaunchedEffect(Unit) {
                if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!hasOverlayPermission) {
                        PermissionRequestScreen(
                            title = "Cần quyền hiển thị",
                            description = "Để hiển thị lời nhắc trên màn hình khóa, ứng dụng cần quyền 'Hiển thị trên các ứng dụng khác'.",
                            onGrantClick = {
                                val intent =
                                    Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:$packageName"),
                                    )
                                startActivity(intent)
                            },
                        )
                    } else if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PermissionRequestScreen(
                            title = "Cần quyền Báo thức",
                            description = "Để nhắc nhở thói quen đúng giây, ứng dụng cần quyền 'Báo thức & nhắc nhở'. Hãy cấp quyền trong màn hình tiếp theo.",
                            onGrantClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val intent = Intent(
                                        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                        Uri.parse("package:$packageName")
                                    )
                                    startActivity(intent)
                                }
                            }
                        )
                    } else if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PermissionRequestScreen(
                            title = "Cần quyền thông báo",
                            description = "Ứng dụng cần quyền gửi thông báo để nhắc nhở bạn thực hiện thói quen đúng giờ.",
                            onGrantClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                        )
                    } else {
                        App()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Khi quay lại app, check lại xem user đã cấp quyền Exact Alarm chưa
        setContent {
            var hasOverlayPermission by remember { mutableStateOf(Settings.canDrawOverlays(this)) }
            var hasNotificationPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    } else true
                )
            }
            var hasExactAlarmPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.canScheduleExactAlarms()
                    } else true
                )
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!hasOverlayPermission) {
                        PermissionRequestScreen(
                            title = "Cần quyền hiển thị",
                            description = "Để hiển thị lời nhắc trên màn hình khóa, ứng dụng cần quyền 'Hiển thị trên các ứng dụng khác'.",
                            onGrantClick = {
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                                startActivity(intent)
                            },
                        )
                    } else if (!hasExactAlarmPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PermissionRequestScreen(
                            title = "Cần quyền Báo thức",
                            description = "Để nhắc nhở thói quen đúng giây, ứng dụng cần quyền 'Báo thức & nhắc nhở'.",
                            onGrantClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:$packageName"))
                                    startActivity(intent)
                                }
                            }
                        )
                    } else if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PermissionRequestScreen(
                            title = "Cần quyền thông báo",
                            description = "Ứng dụng cần quyền gửi thông báo để nhắc nhở bạn thực hiện thói quen đúng giờ.",
                            onGrantClick = {
                                // Trong onResume, việc xin quyền runtime qua launcher có thể hơi phức tạp,
                                // tốt nhất là hướng người dùng vào Setting nếu họ đã từ chối.
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                                startActivity(intent)
                            },
                        )
                    } else {
                        App()
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequestScreen(
    title: String,
    description: String,
    onGrantClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGrantClick) {
            Text("Cấp quyền ngay")
        }
    }
}
