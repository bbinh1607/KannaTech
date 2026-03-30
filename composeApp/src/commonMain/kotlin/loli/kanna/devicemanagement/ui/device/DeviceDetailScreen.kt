package loli.kanna.devicemanagement.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.shared.data.model.device.DeviceDetailResponse
import org.koin.core.parameter.parametersOf

class DeviceDetailScreen(
    private val deviceId: String,
    private val deviceName: String,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<DeviceDetailViewModel> { parametersOf(deviceId) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        
        var showCreateDialog by remember { mutableStateOf(false) }

        LaunchedEffect(state.successMessage) {
            state.successMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessages()
            }
        }

        LaunchedEffect(state.error) {
            state.error?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessages()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(deviceName, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = Color(0xFF24C6D0),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm khu vực")
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFBFBFB))) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF24C6D0),
                    )
                } else if (state.details.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Không có thông tin chi tiết", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(state.details) { detail ->
                            DeviceDetailCard(
                                detail = detail,
                                onClick = {
                                    navigator.push(ComponentDetailScreen(detail.id, detail.area ?: "N/A"))
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateDeviceDetailDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { area ->
                    viewModel.createDeviceDetail(area)
                    showCreateDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeviceDetailDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var area by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm khu vực mới", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Tên khu vực (Area)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Vd: Phòng khách, Bếp...") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(area) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24C6D0)),
                enabled = area.isNotBlank()
            ) {
                Text("Tạo mới")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Color.Gray)
            }
        },
    )
}

@Composable
fun DeviceDetailCard(
    detail: DeviceDetailResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF24C6D0),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Khu vực: ${detail.area ?: "Chưa xác định"}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Trạng thái: ${detail.status ?: "Bình thường"}", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ngày mua: ${detail.buyAt?.split("T")?.get(0) ?: "N/A"}",
                fontSize = 13.sp,
                color = Color.Gray,
            )
            Text(
                text = "Bảo hành đến: ${detail.warranty?.split("T")?.get(0) ?: "N/A"}",
                fontSize = 13.sp,
                color = Color.Gray,
            )
        }
    }
}
