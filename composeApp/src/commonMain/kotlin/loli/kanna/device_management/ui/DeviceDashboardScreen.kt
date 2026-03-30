package loli.kanna.device_management.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import loli.kanna.devicemanagement.ui.DeviceTypeScreen

class DeviceDashboardScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            Text("Hệ thống thiết bị", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("Quản lý tài sản & linh kiện", fontSize = 12.sp, color = Color.Gray)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFFBFBFB))
            ) {
                // Banner Tổng quan
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF24C6D0), Color(0xFF1EAAB3))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text("Tổng thiết bị", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text("1,284", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Icon(
                        Icons.Default.Assessment, 
                        contentDescription = null, 
                        modifier = Modifier.size(80.dp).align(Alignment.CenterEnd).graphicsLayer(alpha = 0.2f),
                        tint = Color.White
                    )
                }

                Text(
                    "Danh mục hệ thống",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CategoryCard("Gia dụng", "12 loại máy", Icons.Default.Home, Color(0xFF6C63FF)) {
                            navigator.push(DeviceTypeScreen("Gia dụng"))
                        }
                    }
                    item {
                        CategoryCard("Công nghiệp", "5 loại máy", Icons.Default.PrecisionManufacturing, Color(0xFFFF6584)) {
                            navigator.push(DeviceTypeScreen("Công nghiệp"))
                        }
                    }
                    item {
                        CategoryCard("Điện tử", "8 loại máy", Icons.Default.Memory, Color(0xFF4CAF50)) {
                            // Link sau
                        }
                    }
                    item {
                        CategoryCard("Vận tải", "3 loại máy", Icons.Default.LocalShipping, Color(0xFFFFA000)) {
                            // Link sau
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, sub: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(sub, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
