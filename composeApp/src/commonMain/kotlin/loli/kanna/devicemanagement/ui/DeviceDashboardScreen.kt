package loli.kanna.devicemanagement.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import loli.kanna.devicemanagement.ui.category.CategoryDialog
import loli.kanna.devicemanagement.ui.category.CategoryViewModel
import loli.kanna.devicemanagement.ui.component.ComponentScreenContent
import loli.kanna.devicemanagement.ui.device.DeviceListScreen

class DeviceDashboardScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Danh mục", "Linh kiện")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Quản lý thiết bị", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(
                                if (selectedTabIndex == 0) "Danh mục hệ thống" else "Tất cả linh kiện",
                                fontSize = 12.sp,
                                color = Color.Gray
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
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                SecondaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFF24C6D0)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> CategoryTabContent()
                    1 -> ComponentScreenContent()
                }
            }
        }
    }

    @Composable
    private fun CategoryTabContent() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<CategoryViewModel>()
        val state by viewModel.state.collectAsState()
        var showAddDialog by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }

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

        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF24C6D0),
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Thẻ tổng quan mini
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("Tổng số danh mục", color = Color.Gray, fontSize = 14.sp)
                                Text(
                                    "${state.categories.size}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF24C6D0),
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF24C6D0).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Default.Dashboard,
                                    contentDescription = null,
                                    tint = Color(0xFF24C6D0),
                                )
                            }
                        }
                    }

                    Text(
                        "Tất cả danh mục",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )

                    if (state.categories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Chưa có danh mục nào", color = Color.Gray)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.categories) { category ->
                                CategoryManageCard(
                                    name = category.name,
                                    description = category.description ?: "",
                                    date = category.createdAt ?: "",
                                    onClick = {
                                        navigator.push(DeviceListScreen(category.id, category.name))
                                    },
                                    onDelete = { viewModel.deleteCategory(category.id) },
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = Color(0xFF24C6D0),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm danh mục")
            }

            SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
        }

        if (showAddDialog) {
            CategoryDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, desc ->
                    viewModel.createCategory(name, desc)
                    showAddDialog = false
                },
            )
        }
    }

    @Composable
    private fun CategoryManageCard(
        name: String,
        description: String,
        date: String,
        onClick: () -> Unit,
        onDelete: () -> Unit,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF24C6D0).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Category, contentDescription = null, tint = Color(0xFF24C6D0))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A),
                    )
                    Text(
                        description,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        maxLines = 1,
                    )
                    if (date.isNotEmpty()) {
                        Text(
                            "Ngày tạo: ${date.take(10)}",
                            fontSize = 10.sp,
                            color = Color.LightGray,
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Xóa",
                        tint = Color.Red.copy(alpha = 0.7f),
                    )
                }
            }
        }
    }
}
