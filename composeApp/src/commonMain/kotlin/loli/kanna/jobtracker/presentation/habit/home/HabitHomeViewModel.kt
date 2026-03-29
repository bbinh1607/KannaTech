package loli.kanna.jobtracker.presentation.habit.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.domain.model.HabitItem
import com.example.shared.domain.model.HabitLog
import com.example.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlin.time.Clock

class HabitHomeViewModel(
    private val repository: HabitRepository,
) : ScreenModel {
    private val _state = MutableStateFlow(HabitHomeState())
    val state = _state.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        _state.update { it.copy(isLoading = true) }
        state
            .map { it.selectedDate }
            .distinctUntilChanged()
            .flatMapLatest { date ->
                repository.getAllHabitItems(date)
            }.onEach { habits ->
                _state.update { it.copy(habits = habits, isLoading = false) }
            }.launchIn(screenModelScope)
    }

    fun onDateSelected(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    fun updateHabitProgress(habitItem: HabitItem, delta: Int) {
        screenModelScope.launch {
            val newProgress = (habitItem.progress + delta).coerceAtLeast(0)
            
            // Kiểm tra xem có phải vừa hoàn thành không (từ chưa xong -> xong)
            val wasCompleted = habitItem.progress >= habitItem.habit.goalValue
            val isNowCompleted = newProgress >= habitItem.habit.goalValue
            
            if (!wasCompleted && isNowCompleted) {
                _state.update { it.copy(completedHabitName = habitItem.habit.name) }
            }

            // Ghi Log
            repository.insertLog(
                HabitLog(
                    habitId = habitItem.habit.id,
                    actionTime = Clock.System.now(),
                    targetDate = _state.value.selectedDate,
                    delta = delta,
                    newValue = newProgress
                )
            )

            repository.toggleHabitCompletion(
                habitItem.habit.id,
                _state.value.selectedDate,
                newProgress,
            )
        }
    }

    fun dismissCompletionDialog() {
        _state.update { it.copy(completedHabitName = null) }
    }
}
