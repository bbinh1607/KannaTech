package loli.kanna.jobtracker.presentation.habit.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.shared.domain.model.HabitLog
import com.example.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class HabitDetailViewModel(
    private val repository: HabitRepository,
    private val habitId: Int
) : ScreenModel {

    private val _state = MutableStateFlow(HabitDetailState())
    val state = _state.asStateFlow()

    init {
        loadHabitDetail()
        
        // Theo dõi và lọc logs mỗi khi logs hoặc filter thay đổi
        _state.map { it.logs to it.selectedFilterAction to it.selectedFilterDate }
            .distinctUntilChanged()
            .onEach { (data, date) ->
                val (logs, action) = data
                val filtered = logs.filter { log ->
                    val matchAction = when (action) {
                        LogFilterAction.ALL -> true
                        LogFilterAction.PLUS -> log.delta > 0
                        LogFilterAction.MINUS -> log.delta < 0
                    }
                    val matchDate = if (date != null) log.targetDate == date else true
                    matchAction && matchDate
                }
                _state.update { it.copy(filteredLogs = filtered) }
            }
            .launchIn(screenModelScope)
    }

    private fun loadHabitDetail() {
        _state.update { it.copy(isLoading = true) }
        
        repository.getHabitByIdFlow(habitId)
            .onEach { habit ->
                _state.update { it.copy(habit = habit) }
            }
            .launchIn(screenModelScope)

        repository.getCompletionHistory(habitId)
            .onEach { history ->
                _state.update { it.copy(history = history) }
            }
            .launchIn(screenModelScope)

        repository.getLogsForHabit(habitId)
            .onEach { logs ->
                _state.update { it.copy(logs = logs, isLoading = false) }
            }
            .launchIn(screenModelScope)
    }

    fun onFilterActionChange(action: LogFilterAction) {
        _state.update { it.copy(selectedFilterAction = action) }
    }

    fun onFilterDateChange(date: LocalDate?) {
        _state.update { it.copy(selectedFilterDate = date) }
    }

    fun deleteHabit() {
        screenModelScope.launch {
            repository.deleteHabit(habitId)
        }
    }
}
