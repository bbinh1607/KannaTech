package loli.kanna.jobtracker.presentation.habit.create

import com.example.shared.domain.model.HabitType
import kotlinx.datetime.LocalDate

data class CreateHabitState(
    val name: String = "",
    val icon: String = "🚶",
    val color: Long = 0xFFFF5722,
    val goalValue: Int = 1,
    val goalUnit: String = "lần",
    val frequency: String = "Hàng ngày",
    val selectedDays: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7), // 1=Thứ 2, 7=CN
    val specificDates: Set<LocalDate> = emptySet(),
    val reminderTime: String? = "09:30",
    val startTime: String? = null,
    val endTime: String? = null,
    val habitType: HabitType = HabitType.BUILD,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val showIconPicker: Boolean = false,
    val showColorPicker: Boolean = false,
    val showDatePicker: Boolean = false,
)
