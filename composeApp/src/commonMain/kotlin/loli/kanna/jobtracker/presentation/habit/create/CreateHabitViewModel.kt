package loli.kanna.jobtracker.presentation.habit.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.domain.model.Habit
import com.example.shared.domain.model.HabitType
import com.example.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CreateHabitViewModel(
    private val repository: HabitRepository,
    private val habitId: Int? = null,
) : ScreenModel {
    private val _state = MutableStateFlow(CreateHabitState())
    val state = _state.asStateFlow()

    val availableIcons = listOf(
        "🚶", "🏃", "💧", "📚", "🧘", "🍎", "💪", "🛌", "🚭", "💊",
        "🎸", "🌱", "☕", "🚴", "🏊", "🏀", "🥗", "⏰", "🧹", "💻", "🏛️"
    )

    val availableColors = listOf(
        0xFFFF5722, 0xFF4CAF50, 0xFF2196F3, 0xFFE91E63, 0xFF9C27B0,
        0xFFFFC107, 0xFF795548, 0xFF607D8B, 0xFF00BCD4, 0xFFFF9800,
        0xFF8BC34A, 0xFF009688
    )

    init {
        if (habitId != null) {
            loadHabitForEdit(habitId)
        }
    }

    private fun loadHabitForEdit(id: Int) {
        screenModelScope.launch {
            repository.getHabitById(id)?.let { habit ->
                _state.update {
                    it.copy(
                        name = habit.name,
                        icon = habit.icon,
                        color = habit.color,
                        goalValue = habit.goalValue,
                        goalUnit = habit.goalUnit,
                        frequency = habit.frequency,
                        selectedDays = if (habit.frequency == "Tùy chỉnh") {
                            habit.repeatDays?.split(",")?.mapNotNull { day -> day.toIntOrNull() }?.toSet() ?: setOf(1, 2, 3, 4, 5, 6, 7)
                        } else {
                            setOf(1, 2, 3, 4, 5, 6, 7)
                        },
                        specificDates = if (habit.frequency == "Ngày cụ thể") {
                            habit.repeatDays?.split(",")?.mapNotNull { dateStr ->
                                try {
                                    LocalDate.parse(dateStr)
                                } catch (e: Exception) {
                                    null
                                }
                            }?.toSet() ?: emptySet()
                        } else {
                            emptySet()
                        },
                        reminderTime = habit.reminderTime,
                        startTime = habit.startTime,
                        endTime = habit.endTime,
                        habitType = habit.habitType,
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun onIconChange(icon: String) {
        _state.update { it.copy(icon = icon, showIconPicker = false) }
    }

    fun onColorChange(color: Long) {
        _state.update { it.copy(color = color, showColorPicker = false) }
    }

    fun onGoalValueChange(value: Int) {
        _state.update { it.copy(goalValue = value.coerceAtLeast(1)) }
    }

    fun onGoalUnitChange(unit: String) {
        _state.update { it.copy(goalUnit = unit) }
    }

    fun onStartTimeChange(time: String?) {
        _state.update { it.copy(startTime = time) }
    }

    fun onEndTimeChange(time: String?) {
        _state.update { it.copy(endTime = time) }
    }

    fun toggleIconPicker(show: Boolean) {
        _state.update { it.copy(showIconPicker = show) }
    }

    fun toggleColorPicker(show: Boolean) {
        _state.update { it.copy(showColorPicker = show) }
    }

    fun toggleDatePicker(show: Boolean) {
        _state.update { it.copy(showDatePicker = show) }
    }

    fun onFrequencyChange(frequency: String) {
        _state.update {
            it.copy(
                frequency = frequency,
                selectedDays = if (frequency == "Hàng ngày") setOf(1, 2, 3, 4, 5, 6, 7) else it.selectedDays,
            )
        }
    }

    fun toggleDay(day: Int) {
        _state.update {
            val newDays =
                if (it.selectedDays.contains(day)) {
                    if (it.selectedDays.size > 1) it.selectedDays - day else it.selectedDays
                } else {
                    it.selectedDays + day
                }
            it.copy(selectedDays = newDays, frequency = "Tùy chỉnh")
        }
    }

    fun addSpecificDate(date: LocalDate) {
        _state.update {
            it.copy(
                specificDates = it.specificDates + date,
                frequency = "Ngày cụ thể",
                showDatePicker = false,
            )
        }
    }

    fun removeSpecificDate(date: LocalDate) {
        _state.update { it.copy(specificDates = it.specificDates - date) }
    }

    fun onReminderTimeChange(time: String) {
        _state.update { it.copy(reminderTime = time) }
    }

    fun onHabitTypeChange(type: HabitType) {
        _state.update { it.copy(habitType = type) }
    }

    fun toggleReminder(enabled: Boolean) {
        _state.update { it.copy(reminderTime = if (enabled) "09:30" else null) }
    }

    fun saveHabit() {
        val currentState = _state.value
        if (currentState.name.isBlank()) return

        screenModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val repeatDaysValue =
                when (currentState.frequency) {
                    "Hàng ngày" -> null
                    "Tùy chỉnh" -> currentState.selectedDays.sorted().joinToString(",")
                    "Ngày cụ thể" -> currentState.specificDates.map { it.toString() }.sorted().joinToString(",")
                    else -> null
                }

            val habit =
                Habit(
                    id = habitId ?: 0,
                    name = currentState.name,
                    icon = currentState.icon,
                    color = currentState.color,
                    goalValue = currentState.goalValue,
                    goalUnit = currentState.goalUnit,
                    frequency = currentState.frequency,
                    repeatDays = repeatDaysValue,
                    reminderTime = currentState.reminderTime,
                    startTime = currentState.startTime,
                    endTime = currentState.endTime,
                    habitType = currentState.habitType,
                    createdAt = kotlin.time.Clock.System.now(),
                )
            repository.upsertHabit(habit)
            _state.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
