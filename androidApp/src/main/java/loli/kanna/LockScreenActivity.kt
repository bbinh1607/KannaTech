package loli.kanna

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared.domain.model.HabitItem
import com.example.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject

class LockScreenActivity : ComponentActivity() {
    private val habitRepository: HabitRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Đảm bảo Activity hiển thị đè lên màn hình khóa
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
        )

        setContent {
            MaterialTheme {
                LockScreenContent(habitRepository) {
                    finish()
                }
            }
        }
    }
}

@Composable
fun LockScreenContent(
    repository: HabitRepository,
    onClose: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var habits by remember { mutableStateOf<List<HabitItem>>(emptyList()) }

    // Sử dụng đường dẫn tuyệt đối để tránh lỗi unresolved 'System'
    val today =
        kotlin.time.Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

    LaunchedEffect(Unit) {
        try {
            habits = repository.getAllHabitItems(today).first()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.98f), // Tăng độ đậm để che màn hình dưới
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Kanna - Thói quen",
                color = Color(0xFF24C6D0),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 60.dp, bottom = 8.dp),
            )

            Text(
                text = "Đừng quên mục tiêu của bạn hôm nay!",
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 32.dp),
            )

            if (habits.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF24C6D0))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(habits) { item ->
                        LockScreenHabitItem(
                            item = item,
                            onToggle = {
                                scope.launch {
                                    repository.toggleHabitCompletion(
                                        item.habit.id,
                                        today,
                                        if (item.isCompleted) 0 else item.habit.goalValue,
                                    )
                                    habits = repository.getAllHabitItems(today).first()
                                }
                            },
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onClose,
                modifier =
                    Modifier
                        .padding(bottom = 40.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24C6D0)),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Đóng", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LockScreenHabitItem(
    item: HabitItem,
    onToggle: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(item.habit.icon, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                item.habit.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
            )
            Text(
                "${item.progress}/${item.habit.goalValue} ${item.habit.goalUnit}",
                color = Color.Gray,
                fontSize = 13.sp,
            )
        }
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (item.isCompleted) Color(0xFF24C6D0) else Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
