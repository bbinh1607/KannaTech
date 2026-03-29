package loli.kanna.jobtracker.presentation.habit.detail

import com.example.shared.domain.model.Habit
import com.example.shared.domain.model.HabitItem
import com.example.shared.domain.model.HabitLog
import kotlinx.datetime.LocalDate

enum class LogFilterAction { ALL, PLUS, MINUS }

data class HabitDetailState(
    val habit: Habit? = null,
    val history: List<HabitItem> = emptyList(),
    val logs: List<HabitLog> = emptyList(),
    val filteredLogs: List<HabitLog> = emptyList(),
    val selectedFilterAction: LogFilterAction = LogFilterAction.ALL,
    val selectedFilterDate: LocalDate? = null,
    val isLoading: Boolean = false
)
