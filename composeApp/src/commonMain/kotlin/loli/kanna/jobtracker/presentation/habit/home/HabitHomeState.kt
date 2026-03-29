package loli.kanna.jobtracker.presentation.habit.home

import com.example.shared.domain.model.HabitItem
import kotlinx.datetime.*
import kotlin.time.Clock

data class HabitHomeState(
    val habits: List<HabitItem> = emptyList(),
    val selectedDate: LocalDate = Clock.System
        .now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date,
    val isLoading: Boolean = false,
    val completedHabitName: String? = null,
)
