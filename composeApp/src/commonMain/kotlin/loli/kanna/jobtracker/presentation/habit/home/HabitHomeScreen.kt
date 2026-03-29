package loli.kanna.jobtracker.presentation.habit.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import com.example.shared.domain.usecase.token.ClearTokenUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import loli.kanna.auth.login.LoginScreen
import loli.kanna.jobtracker.presentation.habit.create.CreateHabitScreen
import loli.kanna.jobtracker.presentation.habit.detail.HabitDetailScreen
import loli.kanna.jobtracker.presentation.habit.home.components.*
import org.koin.compose.koinInject
import kotlin.time.Clock

class HabitHomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HabitHomeViewModel>()
        val state by viewModel.state.collectAsState()
        val clearTokenUseCase = koinInject<ClearTokenUseCase>()
        val scope = rememberCoroutineScope()

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        val completedCount = state.habits.count { it.isCompleted }
        val totalCount = state.habits.size

        val today =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val dateText =
            if (state.selectedDate == today) {
                "ngày hôm nay"
            } else {
                "ngày ${state.selectedDate.dayOfMonth}/${state.selectedDate.monthNumber}/${state.selectedDate.year}"
            }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Kanna Habit",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF24C6D0),
                        fontWeight = FontWeight.Bold,
                    )
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("Trang chủ") },
                        selected = true,
                        onClick = { scope.launch { drawerState.close() } },
                        icon = { Icon(Icons.Default.Home, null) },
                    )
                    NavigationDrawerItem(
                        label = { Text("Hồ sơ") },
                        selected = false,
                        onClick = { /* Navigate to Profile */ },
                        icon = { Icon(Icons.Default.Person, null) },
                    )
                    NavigationDrawerItem(
                        label = { Text("Cài đặt") },
                        selected = false,
                        onClick = { /* Navigate to Settings */ },
                        icon = { Icon(Icons.Default.Settings, null) },
                    )
                    Spacer(Modifier.weight(1f))
                    NavigationDrawerItem(
                        label = { Text("Đăng xuất", color = Color.Red) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                clearTokenUseCase.execute(Unit)
                                navigator.replaceAll(LoginScreen())
                            }
                        },
                        icon = { Icon(Icons.Default.Logout, null, tint = Color.Red) },
                    )
                    Spacer(Modifier.height(12.dp))
                }
            },
        ) {
            Scaffold(
                topBar = {
                    HabitHomeTopBar(onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    })
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navigator.push(CreateHabitScreen()) },
                        containerColor = Color(0xFF24C6D0),
                        shape = CircleShape,
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    }
                },
            ) { padding ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color.White),
                ) {
                    HabitCalendarStrip(
                        selectedDate = state.selectedDate,
                        onDateSelected = { viewModel.onDateSelected(it) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HabitSearchAndFilters()

                    Spacer(modifier = Modifier.height(16.dp))

                    HabitSleepSummaryCard(
                        completedCount = completedCount,
                        totalCount = totalCount,
                    )

                    Text(
                        "Thói quen",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )

                    if (state.habits.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Không có thói quen nào cho ngày này", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding =
                                PaddingValues(
                                    start = 20.dp,
                                    end = 20.dp,
                                    bottom = 80.dp,
                                ),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.habits) { habitItem ->
                                HabitCard(
                                    habitItem = habitItem,
                                    onUpdateProgress = { delta ->
                                        viewModel.updateHabitProgress(habitItem, delta)
                                    },
                                    onClick = {
                                        navigator.push(HabitDetailScreen(habitItem.habit.id))
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
