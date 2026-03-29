package loli.kanna.jobtracker.presentation.habit.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.model.Habit
import kotlinx.datetime.LocalDate

@Composable
fun HabitDetailInfoSection(habit: Habit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Thông tin chi tiết", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF9F9F9))
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HabitDetailInfoRow(
                icon = Icons.Default.Repeat,
                label = "Tần suất",
                value = formatFrequency(habit),
            )

            HabitDetailInfoRow(
                icon = Icons.Default.Flag,
                label = "Mục tiêu",
                value = "${habit.goalValue} ${habit.goalUnit} mỗi ngày",
            )

            HabitDetailInfoRow(
                icon = Icons.Default.Notifications,
                label = "Nhắc nhở",
                value = habit.reminderTime ?: "Không có",
            )

            HabitDetailInfoRow(
                icon = Icons.Default.Info,
                label = "Loại thói quen",
                value = if (habit.habitType.name == "BUILD") "Xây dựng" else "Từ bỏ",
            )
        }
    }
}

@Composable
fun HabitDetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(label, color = Color.Gray, fontSize = 11.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

private fun formatFrequency(habit: Habit): String {
    return when (habit.frequency) {
        "Hàng ngày" -> {
            "Mọi ngày trong tuần"
        }

        "Tùy chỉnh" -> {
            val days = habit.repeatDays?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
            if (days.size == 7) return "Mọi ngày trong tuần"

            val dayNames = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            days.sorted().joinToString(", ") { dayNames.getOrElse(it - 1) { "" } }
        }

        "Ngày cụ thể" -> {
            val dates =
                habit.repeatDays?.split(",")?.mapNotNull {
                    try {
                        LocalDate.parse(it)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

            dates.sorted().joinToString(", ") { "${it.dayOfMonth}/${it.monthNumber}" }
        }

        else -> {
            habit.frequency
        }
    }
}
