package loli.kanna.jobtracker.presentation.habit.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.model.Habit
import com.example.shared.domain.model.HabitItem
import kotlinx.datetime.*
import kotlin.time.Clock

@Composable
fun HabitWeeklyProgressChart(
    habit: Habit,
    history: List<HabitItem>,
) {
    var isMonthlyView by remember { mutableStateOf(false) }
    val today =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

    val daysCount = if (isMonthlyView) 30 else 7
    val displayDays = (0 until daysCount).map { today.minus(it, DateTimeUnit.DAY) }.reversed()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (isMonthlyView) "Tiến độ 30 ngày qua" else "Tiến độ 7 ngày qua",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )

            // Toggle Switch View
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(4.dp),
            ) {
                Text(
                    "Tuần",
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isMonthlyView) Color.White else Color.Transparent)
                            .clickable { isMonthlyView = false }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = if (!isMonthlyView) FontWeight.Bold else FontWeight.Normal,
                    color = if (!isMonthlyView) Color(0xFF24C6D0) else Color.Gray,
                )
                Text(
                    "Tháng",
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isMonthlyView) Color.White else Color.Transparent)
                            .clickable { isMonthlyView = true }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    fontWeight = if (isMonthlyView) FontWeight.Bold else FontWeight.Normal,
                    color = if (isMonthlyView) Color(0xFF24C6D0) else Color.Gray,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(if (isMonthlyView) 4.dp else 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            displayDays.forEach { date ->
                val historyItem = history.find { it.date == date }
                val progressFactor =
                    if (historyItem != null) {
                        (historyItem.progress.toFloat() / habit.goalValue.toFloat()).coerceAtMost(
                            1.2f,
                        )
                    } else {
                        0f
                    }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(progressFactor.coerceAtLeast(0.05f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    if (progressFactor >= 1f) {
                                        Color(0xFF24C6D0)
                                    } else {
                                        Color(
                                            habit.color,
                                        ).copy(alpha = 0.6f)
                                    },
                                ),
                    )
                    if (!isMonthlyView) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(date.dayOfMonth.toString(), fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        if (isMonthlyView) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Từ ${displayDays.first().dayOfMonth}/${displayDays.first().monthNumber} đến ${displayDays.last().dayOfMonth}/${displayDays.last().monthNumber}",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
