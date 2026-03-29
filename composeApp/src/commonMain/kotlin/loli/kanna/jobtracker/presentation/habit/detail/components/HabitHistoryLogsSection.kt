package loli.kanna.jobtracker.presentation.habit.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.shared.domain.model.HabitLog
import kotlinx.datetime.*
import loli.kanna.jobtracker.presentation.habit.detail.LogFilterAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitHistoryLogsSection(
    logs: List<HabitLog>,
    selectedAction: LogFilterAction,
    selectedDate: LocalDate?,
    onActionFilterChange: (LogFilterAction) -> Unit,
    onDateFilterChange: (LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nhật ký hoạt động", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            
            // Nút chọn ngày lọc
            IconButton(onClick = { showDatePicker = true }) {
                Icon(
                    Icons.Default.FilterList, 
                    contentDescription = "Lọc",
                    tint = if (selectedDate != null) Color(0xFF24C6D0) else Color.Gray
                )
            }
        }

        // Thanh lọc loại hành động (+ / - / Tất cả)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF5F5F5))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChipItem(
                label = "Tất cả",
                isSelected = selectedAction == LogFilterAction.ALL,
                modifier = Modifier.weight(1f)
            ) { onActionFilterChange(LogFilterAction.ALL) }
            
            FilterChipItem(
                label = "+ Tăng",
                isSelected = selectedAction == LogFilterAction.PLUS,
                modifier = Modifier.weight(1f)
            ) { onActionFilterChange(LogFilterAction.PLUS) }
            
            FilterChipItem(
                label = "- Giảm",
                isSelected = selectedAction == LogFilterAction.MINUS,
                modifier = Modifier.weight(1f)
            ) { onActionFilterChange(LogFilterAction.MINUS) }
        }

        // Hiển thị tag ngày đang lọc (nếu có)
        if (selectedDate != null) {
            InputChip(
                selected = true,
                onClick = { onDateFilterChange(null) },
                label = { Text("Ngày: ${selectedDate.dayOfMonth}/${selectedDate.monthNumber}") },
                trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) },
                colors = InputChipDefaults.inputChipColors(selectedContainerColor = Color(0xFF24C6D0).copy(alpha = 0.1f))
            )
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Không có kết quả lọc phù hợp", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                logs.forEach { log ->
                    LogItem(log)
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
                        onDateFilterChange(date)
                    }
                    showDatePicker = false
                }) { Text("Lọc") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    onDateFilterChange(null)
                    showDatePicker = false 
                }) { Text("Xóa lọc") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF24C6D0) else Color.Gray
        )
    }
}

@Composable
fun LogItem(log: HabitLog) {
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = log.actionTime.toLocalDateTime(timeZone)
    val timeStr = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    val dateStr = "${localDateTime.dayOfMonth}/${localDateTime.monthNumber}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (log.delta > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (log.delta > 0) Icons.Default.Add else Icons.Default.Remove,
                contentDescription = null,
                tint = if (log.delta > 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            val actionText = if (log.delta > 0) "đã tăng" else "đã giảm"
            Text(
                text = "Bạn $actionText tiến độ lên ${log.newValue}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Cho ngày ${log.targetDate.dayOfMonth}/${log.targetDate.monthNumber}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(timeStr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(dateStr, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
