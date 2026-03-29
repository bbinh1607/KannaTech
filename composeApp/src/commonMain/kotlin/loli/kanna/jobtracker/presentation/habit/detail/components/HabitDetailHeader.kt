package loli.kanna.jobtracker.presentation.habit.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.model.Habit

@Composable
fun HabitDetailHeader(habit: Habit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(habit.color).copy(alpha = 0.1f),
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp),
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(habit.icon, fontSize = 32.sp)
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    habit.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                )
                Text(
                    "${habit.goalValue} ${habit.goalUnit} / ${habit.frequency}",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
