package loli.kanna.devicemanagement.ui.device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
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
import com.example.shared.data.model.device.ComponentDetailCreateRequest
import com.example.shared.data.model.device.ComponentDetailResponse
import com.example.shared.data.model.device.ComponentDetailUpdateRequest
import com.example.shared.data.model.device.DeviceDetailResponse
import org.koin.core.parameter.parametersOf

class ComponentDetailScreen(
    private val id: String,
    private val title: String,
    private val isFromComponent: Boolean = false,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getScreenModel<ComponentDetailViewModel> { parametersOf(id, isFromComponent) }
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        var selectedDetailForEdit by remember { mutableStateOf<ComponentDetailResponse?>(null) }
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
                    title = {
                        Column {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                if (isFromComponent) "Chi tiết linh kiện hệ thống" else "Linh kiện tại khu vực",
                                fontSize = 12.sp,
                                color = Color.Gray,
                            )
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
            floatingActionButton = {
                if (isFromComponent) {
                    FloatingActionButton(
                        onClick = { showCreateDialog = true },
                        containerColor = Color(0xFF24C6D0),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Thêm linh kiện")
                    }
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
                            Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chưa có linh kiện nào", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(state.details) { detail ->
                            ComponentDetailCard(
                                detail = detail,
                                onEdit = {
                                    selectedDetailForEdit = detail
                                    detail.component?.deviceId?.let {
                                        viewModel.getAllDeviceDetails()
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }

        if (selectedDetailForEdit != null) {
            UpdateComponentDetailDialog(
                detail = selectedDetailForEdit!!,
                deviceDetails = state.deviceDetails,
                onDismiss = { selectedDetailForEdit = null },
                onConfirm = { request ->
                    viewModel.updateComponentDetail(selectedDetailForEdit!!.id, request)
                    selectedDetailForEdit = null
                },
            )
        }

        if (showCreateDialog) {
            CreateComponentDetailDialog(
                componentId = id,
                deviceDetails = state.deviceDetails,
                onDismiss = { showCreateDialog = false },
                onConfirm = { request ->
                    viewModel.createComponentDetail(request)
                    showCreateDialog = false
                },
            )
        }
    }
}

@Composable
fun ComponentDetailCard(
    detail: ComponentDetailResponse,
    onEdit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        tint = Color(0xFF24C6D0),
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = detail.component?.name ?: "Linh kiện không tên",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Sửa",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = detail.component?.description ?: "Không có mô tả",
                fontSize = 14.sp,
                color = Color.Gray,
            )

            val deviceName = detail.deviceDetail?.device?.name
            val areaName = detail.deviceDetail?.area

            if (deviceName != null || areaName != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Devices,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text =
                            buildString {
                                if (deviceName != null) append(deviceName)
                                if (deviceName != null && areaName != null) append(" - ")
                                if (areaName != null) append(areaName)
                            },
                        fontSize = 13.sp,
                        color = Color(0xFF24C6D0),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoItem(
                    label = "Trạng thái",
                    value = detail.status ?: "N/A",
                    color =
                        if (detail.status == "Đang sử dụng" || detail.status == "Bình thường") {
                            Color(
                                0xFF4CAF50,
                            )
                        } else {
                            Color.Red
                        },
                )
                InfoItem(label = "Giá", value = "${detail.price ?: 0.0} đ")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoItem(label = "Ngày mua", value = detail.buyAt?.split("T")?.get(0) ?: "N/A")
                InfoItem(
                    label = "Hết hạn",
                    value = detail.expirationDate?.split("T")?.get(0) ?: "N/A",
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateComponentDetailDialog(
    detail: ComponentDetailResponse,
    deviceDetails: List<DeviceDetailResponse>,
    onDismiss: () -> Unit,
    onConfirm: (ComponentDetailUpdateRequest) -> Unit,
) {
    var status by remember { mutableStateOf(detail.status ?: "") }
    var price by remember { mutableStateOf(detail.price?.toString() ?: "0") }
    var buyAt by remember { mutableStateOf(detail.buyAt?.split("T")?.get(0) ?: "") }
    var expirationDate by remember {
        mutableStateOf(
            detail.expirationDate?.split("T")?.get(0) ?: "",
        )
    }
    var selectedDeviceDetail by remember { mutableStateOf(detail.deviceDetail) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cập nhật chi tiết linh kiện", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Trạng thái") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
                item {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Giá") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
                item {
                    OutlinedTextField(
                        value = buyAt,
                        onValueChange = { buyAt = it },
                        label = { Text("Ngày mua (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
                item {
                    OutlinedTextField(
                        value = expirationDate,
                        onValueChange = { expirationDate = it },
                        label = { Text("Ngày hết hạn (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )
                }
                item {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                    ) {
                        OutlinedTextField(
                            value = selectedDeviceDetail?.area ?: "Chọn khu vực",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("thiết bị") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            deviceDetails.forEach { devDetail ->
                                DropdownMenuItem(
                                    text = { Text(devDetail.area ?: "N/A") },
                                    onClick = {
                                        selectedDeviceDetail = devDetail
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ComponentDetailUpdateRequest(
                            status = status,
                            price = price.toDoubleOrNull(),
                            buyAt = if (buyAt.isNotEmpty()) "${buyAt}T00:00:00" else null,
                            expirationDate = if (expirationDate.isNotEmpty()) "${expirationDate}T00:00:00" else null,
                            deviceDetailId = selectedDeviceDetail?.id,
                            componentId = detail.component?.id,
                        ),
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24C6D0)),
            ) {
                Text("Cập nhật")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Color.Gray)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateComponentDetailDialog(
    componentId: String,
    deviceDetails: List<DeviceDetailResponse>,
    onDismiss: () -> Unit,
    onConfirm: (ComponentDetailCreateRequest) -> Unit,
) {
    var price by remember { mutableStateOf("") }
    var selectedDeviceDetail by remember { mutableStateOf<DeviceDetailResponse?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo mới chi tiết linh kiện", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        value = selectedDeviceDetail?.area ?: "chọn thiết bị",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("chọn thiết bị") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        deviceDetails.forEach { devDetail ->
                            DropdownMenuItem(
                                text = { Text(devDetail.area ?: "N/A") },
                                onClick = {
                                    selectedDeviceDetail = devDetail
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ComponentDetailCreateRequest(
                            componentId = componentId,
                            price = price.toDoubleOrNull(),
                            deviceDetailId = selectedDeviceDetail?.id,
                        ),
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24C6D0)),
                enabled = price.isNotEmpty() && selectedDeviceDetail != null,
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
fun InfoItem(
    label: String,
    value: String,
    color: Color = Color.Black,
) {
    Column {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = color)
    }
}
