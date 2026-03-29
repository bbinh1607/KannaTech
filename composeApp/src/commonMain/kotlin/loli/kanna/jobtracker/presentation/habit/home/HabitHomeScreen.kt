package loli.kanna.jobtracker.presentation.habit.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import loli.kanna.jobtracker.presentation.habit.create.CreateHabitScreen
import loli.kanna.jobtracker.presentation.habit.detail.HabitDetailScreen
import loli.kanna.jobtracker.presentation.habit.home.components.*

class HabitHomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HabitHomeViewModel>()
        val state by viewModel.state.collectAsState()

        val completedCount = state.habits.count { it.isCompleted }
        val totalCount = state.habits.size

        // Xác định văn bản hiển thị ngày dựa trên selectedDate trong state
        val today = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val dateText = if (state.selectedDate == today) {
            "ngày hôm nay"
        } else {
            "ngày ${state.selectedDate.dayOfMonth}/${state.selectedDate.monthNumber}/${state.selectedDate.year}"
        }

        // Hiển thị Dialog chúc mừng
        state.completedHabitName?.let { habitName ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissCompletionDialog() },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissCompletionDialog() }) {
                        Text("Tuyệt vời!", color = Color(0xFF24C6D0))
                    }
                },
                title = { Text("Chúc mừng! 🎉") },
                text = {
                    Text("Bạn đã hoàn thành thói quen \"$habitName\" của $dateText. Hãy tiếp tục phát huy nhé!")
                },
                shape = MaterialTheme.shapes.large,
                containerColor = Color.White,
            )
        }

        Scaffold(
            topBar = {
                HabitHomeTopBar()
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
