package loli.kanna.devicemanagement.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.shared.data.model.device.DeviceResponse
import org.koin.core.parameter.parametersOf

class DeviceListScreen(
    private val categoryId: String,
    private val categoryName: String,
) : Screen {
    override val key: ScreenKey = "DeviceListScreen_$categoryId"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<DeviceListViewModel> { parametersOf(categoryId) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        var showAddDialog by remember { mutableStateOf(false) }
        var deviceToDelete by remember { mutableStateOf<DeviceResponse?>(null) }

        // Mỗi lần vào màn hình, kiểm tra nếu chưa có data thì load
        LaunchedEffect(Unit) {
            viewModel.getDevices()
        }

        // Theo dõi thông báo
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
                    title = {
                        Column {
                            Text(categoryName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Danh sách thiết bị", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF24C6D0),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm thiết bị")
                }
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFFBFBFB))) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF24C6D0),
                    )
                } else if (state.devices.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.Devices,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chưa có thiết bị nào", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.devices) { device ->
                            DeviceCard(
                                device = device,
                                onClick = { 
                                    navigator.push(DeviceDetailScreen(device.id, device.name))
                                },
                                onDelete = { deviceToDelete = device }
                            )
                        }
                    }
                }
            }

            if (showAddDialog) {
                DeviceDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { name, desc ->
                        viewModel.createDevice(name, desc)
                        showAddDialog = false
                    },
                )
            }

            // Dialog xác nhận xóa
            if (deviceToDelete != null) {
                AlertDialog(
                    onDismissRequest = { deviceToDelete = null },
                    title = { Text("Xác nhận xóa") },
                    text = { Text("Bạn có chắc chắn muốn xóa thiết bị '${deviceToDelete?.name}' không?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                deviceToDelete?.let { viewModel.deleteDevice(it.id) }
                                deviceToDelete = null
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Xóa")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { deviceToDelete = null }) {
                            Text("Hủy")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: DeviceResponse,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF24C6D0).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Devices, contentDescription = null, tint = Color(0xFF24C6D0))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(device.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        device.description ?: "Không có mô tả",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.LightGray.copy(alpha = 0.5f))
                }

                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
            }

            if (device.listComponent.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "${device.listComponent.size} linh kiện: ${device.listComponent.joinToString { it.name }}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
