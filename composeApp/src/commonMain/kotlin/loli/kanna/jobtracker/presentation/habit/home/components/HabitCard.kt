package loli.kanna.jobtracker.presentation.habit.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.model.HabitItem
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun HabitCard(
    habitItem: HabitItem,
    onUpdateProgress: (Int) -> Unit,
    onClick: () -> Unit,
) {
    val habit = habitItem.habit
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val threshold = 70f // Khoảng cách để kích hoạt hành động

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF9F9F9))
            .clickable { onClick() }
    ) {
        // Nền phía sau khi swipe
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Giảm",
                tint = if (offsetX.value > 0) Color.Red else Color.Transparent,
                modifier = Modifier.size(24.dp)
            )

            Icon(
                Icons.Default.Add,
                contentDescription = "Tăng",
                tint = if (offsetX.value < 0) Color(0xFF24C6D0) else Color.Transparent,
                modifier = Modifier.size(24.dp)
            )
        }

        // Nội dung chính của Card
        Row(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9))
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            offsetX.snapTo(offsetX.value + delta)
                        }
                    },
                    onDragStopped = {
                        if (offsetX.value > threshold) {
                            onUpdateProgress(-1)
                        } else if (offsetX.value < -threshold) {
                            onUpdateProgress(1)
                        }
                        scope.launch {
                            offsetX.animateTo(0f)
                        }
                    }
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon thói quen
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(habit.color).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(habit.icon)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Thông tin chính
            Column(modifier = Modifier.weight(1f)) {
                Text(habit.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "${habitItem.progress}/${habit.goalValue} ${habit.goalUnit}",
                    fontSize = 12.sp,
                    color = if (habitItem.progress >= habit.goalValue) Color(0xFF24C6D0) else Color.Gray,
                )
            }

            // Thời gian thực hiện (Cột bên phải)
            if (habit.startTime != null || habit.endTime != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = habit.startTime ?: "//",
                        fontSize = 11.sp,
                        color = if (habit.startTime != null) Color(0xFF24C6D0) else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = habit.endTime ?: "//",
                        fontSize = 11.sp,
                        color = if (habit.endTime != null) Color(0xFF24C6D0) else Color.LightGray,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }

            // Icon hoàn thành
            if (habitItem.progress >= habit.goalValue) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF24C6D0),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
