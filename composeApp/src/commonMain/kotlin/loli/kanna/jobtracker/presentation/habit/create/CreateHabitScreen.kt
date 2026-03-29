package loli.kanna.jobtracker.presentation.habit.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.shared.domain.model.HabitType
import kotlinx.datetime.*
import loli.kanna.jobtracker.presentation.habit.create.components.*
import org.koin.core.parameter.parametersOf

class CreateHabitScreen(
    private val habitId: Int? = null,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<CreateHabitViewModel> { parametersOf(habitId) }
        val state by viewModel.state.collectAsState()
        val focusManager = LocalFocusManager.current

        var showTimePicker by remember { mutableStateOf(false) }
        var timePickerType by remember { mutableStateOf("reminder") } // "reminder", "start", "end"
        
        val timePickerState =
            rememberTimePickerState(initialHour = 9, initialMinute = 30, is24Hour = true)

        val datePickerState = rememberDatePickerState()

        val isEditMode = habitId != null

        LaunchedEffect(state.isSaved) {
            if (state.isSaved) navigator.pop()
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            if (!isEditMode) "Tạo thói quen mới" else "Chỉnh sửa thói quen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                )
            },
            bottomBar = {
                Button(
                    onClick = { viewModel.saveHabit() },
                    modifier = Modifier.fillMaxWidth().padding(20.dp).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24C6D0)),
                    enabled = !state.isSaving,
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Text(
                            if (!isEditMode) "Thêm thói quen" else "Lưu thay đổi",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(Color.White)
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    // TÊN THÓI QUEN
                    Column {
                        CreateHabitSectionTitle("TÊN THÓI QUEN")
                        TextField(
                            value = state.name,
                            onValueChange = { viewModel.onNameChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("VD: Đi hội thảo", color = Color.Gray) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions =
                                KeyboardActions(
                                    onDone = { focusManager.clearFocus() },
                                ),
                            colors =
                                TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color(0xFF24C6D0),
                                    unfocusedIndicatorColor = Color.LightGray,
                                ),
                            textStyle =
                                LocalTextStyle.current.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }

                    // BIỂU TƯỢNG VÀ MÀU SẮC
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CreateHabitSelectionBox(
                            "Biểu tượng",
                            state.icon,
                            icon = state.icon,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.toggleIconPicker(true) },
                        )
                        CreateHabitSelectionBox(
                            "Màu sắc",
                            "Chọn màu",
                            color = Color(state.color),
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.toggleColorPicker(true) },
                        )
                    }

                    // THỜI GIAN THỰC HIỆN - MỚI
                    Column {
                        CreateHabitSectionTitle("THỜI GIAN THỰC HIỆN")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Giờ bắt đầu
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF9F9F9))
                                    .clickable { 
                                        timePickerType = "start"
                                        showTimePicker = true 
                                    }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("Bắt đầu", fontSize = 10.sp, color = Color.Gray)
                                    Text(state.startTime ?: "--:--", fontWeight = FontWeight.Bold, color = if (state.startTime != null) Color(0xFF24C6D0) else Color.Gray)
                                }
                            }
                            
                            // Giờ kết thúc
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF9F9F9))
                                    .clickable { 
                                        timePickerType = "end"
                                        showTimePicker = true 
                                    }
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text("Kết thúc", fontSize = 10.sp, color = Color.Gray)
                                    Text(state.endTime ?: "--:--", fontWeight = FontWeight.Bold, color = if (state.endTime != null) Color(0xFF24C6D0) else Color.Gray)
                                }
                            }
                        }
                    }

                    // MỤC TIÊU - DISABLED IN EDIT MODE
                    Column(modifier = Modifier.alpha(if (isEditMode) 0.5f else 1f)) {
                        CreateHabitSectionTitle("MỤC TIÊU")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            OutlinedTextField(
                                value = state.goalValue.toString(),
                                onValueChange = { newValue ->
                                    if (!isEditMode) {
                                        newValue.toIntOrNull()?.let { viewModel.onGoalValueChange(it) }
                                    }
                                },
                                modifier = Modifier.width(80.dp),
                                enabled = !isEditMode,
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next,
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF24C6D0),
                                        unfocusedBorderColor = Color.LightGray,
                                    ),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            )

                            OutlinedTextField(
                                value = state.goalUnit,
                                onValueChange = { if (!isEditMode) viewModel.onGoalUnitChange(it) },
                                modifier = Modifier.weight(1f),
                                enabled = !isEditMode,
                                placeholder = {
                                    Text(
                                        "Đơn vị (VD: cốc nước, km...)",
                                        fontSize = 14.sp,
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                    OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF24C6D0),
                                        unfocusedBorderColor = Color.LightGray,
                                    ),
                            )
                        }
                    }

                    // TẦN SUẤT & NGÀY CỤ THỂ - DISABLED IN EDIT MODE
                    Column(modifier = Modifier.alpha(if (isEditMode) 0.5f else 1f)) {
                        CreateHabitSectionTitle("TẦN SUẤT & THỜI GIAN")
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF5F5F5))
                                    .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            CreateHabitSegmentButton(
                                "Hàng ngày",
                                isSelected = state.frequency == "Hàng ngày",
                                modifier = Modifier.weight(1f),
                            ) { if (!isEditMode) viewModel.onFrequencyChange("Hàng ngày") }
                            CreateHabitSegmentButton(
                                "Tùy chỉnh",
                                isSelected = state.frequency == "Tùy chỉnh",
                                modifier = Modifier.weight(1f),
                            ) { if (!isEditMode) viewModel.onFrequencyChange("Tùy chỉnh") }
                            CreateHabitSegmentButton(
                                "Cụ thể",
                                isSelected = state.frequency == "Ngày cụ thể",
                                modifier = Modifier.weight(1f),
                            ) { if (!isEditMode) viewModel.onFrequencyChange("Ngày cụ thể") }
                        }

                        if (state.frequency == "Tùy chỉnh") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                listOf(
                                    "T2",
                                    "T3",
                                    "T4",
                                    "T5",
                                    "T6",
                                    "T7",
                                    "CN",
                                ).forEachIndexed { index, day ->
                                    val dayNum = index + 1
                                    val isSelected = state.selectedDays.contains(dayNum)
                                    Box(
                                        modifier =
                                            Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) {
                                                        Color(0xFF24C6D0)
                                                    } else {
                                                        Color(
                                                            0xFFF5F5F5,
                                                        )
                                                    },
                                                ).clickable(enabled = !isEditMode) { viewModel.toggleDay(dayNum) },
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = day,
                                            color = if (isSelected) Color.White else Color.Gray,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            }
                        }

                        if (state.frequency == "Ngày cụ thể") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Chọn các ngày bạn muốn thực hiện (Ví dụ: ngày 1 và 15 tháng 3):",
                                fontSize = 11.sp,
                                color = Color.Gray,
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.toggleDatePicker(true) },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isEditMode,
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            Color(
                                                0xFFF5F5F5,
                                            ),
                                        contentColor = Color.Black,
                                    ),
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thêm ngày cụ thể")
                            }

                            if (state.specificDates.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(state.specificDates.toList().sorted()) { date ->
                                        Surface(
                                            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                                            color = Color(0xFF24C6D0).copy(alpha = 0.1f),
                                        ) {
                                            Row(
                                                modifier =
                                                    Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp,
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    "${date.dayOfMonth}/${date.monthNumber}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF24C6D0),
                                                )
                                                if (!isEditMode) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = null,
                                                        modifier =
                                                            Modifier.size(14.dp).clickable {
                                                                viewModel.removeSpecificDate(date)
                                                            },
                                                        tint = Color(0xFF24C6D0),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // NHẮC NHỞ & THỜI GIAN - DISABLED IN EDIT MODE
                    Column(modifier = Modifier.alpha(if (isEditMode) 0.5f else 1f)) {
                        CreateHabitSectionTitle("NHẮC NHỞ")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "Thời gian nhắc nhở",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Switch(
                                        checked = state.reminderTime != null,
                                        onCheckedChange = { if (!isEditMode) viewModel.toggleReminder(it) },
                                        enabled = !isEditMode,
                                        colors =
                                            SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = Color(0xFF24C6D0),
                                            ),
                                    )
                                }
                                if (state.reminderTime != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                                .clickable(enabled = !isEditMode) { 
                                                    timePickerType = "reminder"
                                                    showTimePicker = true 
                                                }
                                                .padding(12.dp),
                                    ) {
                                        Text(
                                            "Bắt đầu lúc: ${state.reminderTime}",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF24C6D0),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // LOẠI THÓI QUEN - DISABLED IN EDIT MODE
                    Column(modifier = Modifier.alpha(if (isEditMode) 0.5f else 1f)) {
                        CreateHabitSectionTitle("LOẠI THÓI QUEN")
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFFF5F5F5))
                                    .padding(4.dp),
                        ) {
                            CreateHabitSegmentButton(
                                "Xây dựng",
                                isSelected = state.habitType == HabitType.BUILD,
                                modifier = Modifier.weight(1f),
                            ) { if (!isEditMode) viewModel.onHabitTypeChange(HabitType.BUILD) }
                            CreateHabitSegmentButton(
                                "Từ bỏ",
                                isSelected = state.habitType == HabitType.QUIT,
                                modifier = Modifier.weight(1f),
                            ) { if (!isEditMode) viewModel.onHabitTypeChange(HabitType.QUIT) }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }

                // DATE PICKER DIALOG
                if (state.showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { viewModel.toggleDatePicker(false) },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    val date =
                                        Instant
                                            .fromEpochMilliseconds(it)
                                            .toLocalDateTime(TimeZone.UTC)
                                            .date
                                    viewModel.addSpecificDate(date)
                                }
                            }) { Text("Chọn", color = Color(0xFF24C6D0)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { viewModel.toggleDatePicker(false) }) {
                                Text(
                                    "Hủy",
                                )
                            }
                        },
                    ) { DatePicker(state = datePickerState) }
                }

                // TIME PICKER DIALOG
                if (showTimePicker) {
                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val hour = timePickerState.hour.toString().padStart(2, '0')
                                val minute = timePickerState.minute.toString().padStart(2, '0')
                                val time = "$hour:$minute"
                                
                                when(timePickerType) {
                                    "reminder" -> viewModel.onReminderTimeChange(time)
                                    "start" -> viewModel.onStartTimeChange(time)
                                    "end" -> viewModel.onEndTimeChange(time)
                                }
                                showTimePicker = false
                            }) { Text("Xác nhận", color = Color(0xFF24C6D0)) }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                when(timePickerType) {
                                    "start" -> viewModel.onStartTimeChange(null)
                                    "end" -> viewModel.onEndTimeChange(null)
                                }
                                showTimePicker = false
                            }) { Text("Xóa", color = Color.Red) }
                        },
                        text = { TimePicker(state = timePickerState) },
                    )
                }

                // PICKERS (Icon/Color)
                if (state.showIconPicker) {
                    PickerModal(
                        "Chọn biểu tượng",
                        onDismiss = { viewModel.toggleIconPicker(false) },
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(viewModel.availableIcons) { icon ->
                                Box(
                                    modifier =
                                        Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (state.icon == icon) {
                                                    Color(0xFF24C6D0).copy(alpha = 0.1f)
                                                } else {
                                                    Color(
                                                        0xFFF5F5F5,
                                                    )
                                                },
                                            ).border(
                                                2.dp,
                                                if (state.icon == icon) Color(0xFF24C6D0) else Color.Transparent,
                                                RoundedCornerShape(12.dp),
                                            ).clickable { viewModel.onIconChange(icon) },
                                    contentAlignment = Alignment.Center,
                                ) { Text(icon, fontSize = 24.sp) }
                            }
                        }
                    }
                }

                if (state.showColorPicker) {
                    PickerModal(
                        "Chọn màu sắc",
                        onDismiss = { viewModel.toggleColorPicker(false) },
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(viewModel.availableColors) { colorValue ->
                                Box(
                                    modifier =
                                        Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorValue))
                                            .border(
                                                3.dp,
                                                if (state.color == colorValue) Color.Black else Color.Transparent,
                                                CircleShape,
                                            ).clickable { viewModel.onColorChange(colorValue) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PickerModal(
        title: String,
        onDismiss: () -> Unit,
        content: @Composable () -> Unit,
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Box(modifier = Modifier.height(300.dp)) { content() } },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        "Đóng",
                        color = Color(0xFF24C6D0),
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
        )
    }
}
