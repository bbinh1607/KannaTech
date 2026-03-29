package loli.kanna.jobtracker.presentation.habit.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import loli.kanna.jobtracker.presentation.habit.create.CreateHabitScreen
import loli.kanna.jobtracker.presentation.habit.detail.components.HabitDetailHeader
import loli.kanna.jobtracker.presentation.habit.detail.components.HabitDetailInfoSection
import loli.kanna.jobtracker.presentation.habit.detail.components.HabitHistoryLogsSection
import loli.kanna.jobtracker.presentation.habit.detail.components.HabitWeeklyProgressChart
import org.koin.core.parameter.parametersOf

class HabitDetailScreen(
    private val habitId: Int,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HabitDetailViewModel> { parametersOf(habitId) }
        val state by viewModel.state.collectAsState()

        var showDatePicker by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Chi tiết thói quen", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.Event, contentDescription = "Chọn ngày")
                        }
                        IconButton(onClick = {
                            navigator.push(CreateHabitScreen(habitId))
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                        }
                        IconButton(onClick = {
                            viewModel.deleteHabit()
                            navigator.pop()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                        }
                    },
                )
            },
        ) { padding ->
            state.habit?.let { habit ->
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                ) {
                    HabitDetailHeader(habit = habit)

                    Spacer(modifier = Modifier.height(32.dp))

                    HabitWeeklyProgressChart(
                        habit = habit,
                        history = state.history,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    HabitHistoryLogsSection(
                        logs = state.filteredLogs,
                        selectedAction = state.selectedFilterAction,
                        selectedDate = state.selectedFilterDate,
                        onActionFilterChange = { viewModel.onFilterActionChange(it) },
                        onDateFilterChange = { viewModel.onFilterDateChange(it) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    HabitDetailInfoSection(habit = habit)
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
