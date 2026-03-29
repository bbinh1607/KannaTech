package com.example.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.shared.db.AppDatabase
import com.example.shared.db.Habit_entity
import com.example.shared.db.Habit_log
import com.example.shared.domain.model.Habit
import com.example.shared.domain.model.HabitItem
import com.example.shared.domain.model.HabitLog
import com.example.shared.domain.model.HabitType
import com.example.shared.domain.reminder.ReminderManager
import com.example.shared.domain.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class HabitRepositoryImpl(
    private val db: AppDatabase,
    private val reminderManager: ReminderManager? = null,
) : HabitRepository {
    private val queries = db.habitQueries

    override fun getAllHabitItems(date: LocalDate): Flow<List<HabitItem>> {
        val habitsFlow = queries.getAllHabits().asFlow().mapToList(Dispatchers.IO)
        val completionsFlow =
            queries.getCompletionByDate(date.toString()).asFlow().mapToList(Dispatchers.IO)

        return combine(habitsFlow, completionsFlow) { habits, completions ->
            habits
                .filter { habitEntity ->
                    isHabitActiveOnDate(habitEntity, date)
                }.map { habitEntity ->
                    val completion = completions.find { it.habitId == habitEntity.id }
                    HabitItem(
                        habit = habitEntity.toDomain(),
                        isCompleted = (completion?.progressValue ?: 0) >= habitEntity.goalValue,
                        progress = (completion?.progressValue ?: 0).toInt(),
                        date = date,
                    )
                }
        }
    }

    override fun getHabitByIdFlow(id: Int): Flow<Habit?> =
        queries.getHabitById(id.toLong())
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }

    override fun getCompletionHistory(habitId: Int): Flow<List<HabitItem>> {
        val habitFlow =
            queries.getHabitById(habitId.toLong()).asFlow().mapToOneOrNull(Dispatchers.IO)
        val historyFlow =
            queries
                .getCompletionHistoryForHabit(habitId.toLong())
                .asFlow()
                .mapToList(Dispatchers.IO)

        return combine(habitFlow, historyFlow) { habitEntity, history ->
            if (habitEntity == null) return@combine emptyList()
            history.map { completion ->
                HabitItem(
                    habit = habitEntity.toDomain(),
                    isCompleted = completion.progressValue >= habitEntity.goalValue,
                    progress = completion.progressValue.toInt(),
                    date = LocalDate.parse(completion.completedDate),
                )
            }
        }
    }

    override suspend fun insertLog(log: HabitLog) {
        queries.insertLog(
            habitId = log.habitId.toLong(),
            actionTime = log.actionTime.toEpochMilliseconds(),
            targetDate = log.targetDate.toString(),
            delta = log.delta.toLong(),
            newValue = log.newValue.toLong(),
        )
    }

    override fun getLogsForHabit(habitId: Int): Flow<List<HabitLog>> =
        queries
            .getLogsForHabit(habitId.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { logs ->
                logs.map { it.toDomain() }
            }

    private fun isHabitActiveOnDate(
        habit: Habit_entity,
        date: LocalDate,
    ): Boolean {
        return when (habit.frequency) {
            "Hàng ngày" -> {
                true
            }

            "Tùy chỉnh" -> {
                val repeatDaysStr = habit.repeatDays ?: return true
                val repeatDays =
                    repeatDaysStr
                        .split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }
                val dayOfWeek = date.dayOfWeek.ordinal + 1
                repeatDays.contains(dayOfWeek)
            }

            "Ngày cụ thể" -> {
                val repeatDaysStr = habit.repeatDays ?: return false
                val specificDates = repeatDaysStr.split(",").filter { it.isNotBlank() }
                specificDates.contains(date.toString())
            }

            else -> {
                true
            }
        }
    }

    override suspend fun getHabitById(id: Int): Habit? = queries.getHabitById(id.toLong()).executeAsOneOrNull()?.toDomain()

    override suspend fun upsertHabit(habit: Habit) {
        // LOG KIỂM TRA:
        println("HabitRepository: Đang lưu thói quen ${habit.name}")
        
        queries.insertHabit(
            id = if (habit.id == 0) null else habit.id.toLong(),
            name = habit.name,
            icon = habit.icon,
            color = habit.color,
            goalValue = habit.goalValue.toLong(),
            goalUnit = habit.goalUnit,
            frequency = habit.frequency,
            repeatDays = habit.repeatDays,
            reminderTime = habit.reminderTime,
            startTime = habit.startTime,
            endTime = habit.endTime,
            habitType = habit.habitType.name,
            createdAt = habit.createdAt.toEpochMilliseconds(),
        )

        if (habit.reminderTime != null) {
            val savedHabit = if (habit.id == 0) {
                // Lấy thói quen mới nhất vừa lưu
                queries.getAllHabits().executeAsList().maxByOrNull { it.createdAt }?.toDomain()
            } else {
                habit
            }
            
            if (reminderManager == null) {
                println("HabitRepository: LỖI - reminderManager đang bị NULL!")
            }
            
            savedHabit?.let { 
                println("HabitRepository: Đang gọi scheduleReminder cho ${it.name}")
                reminderManager?.scheduleReminder(it) 
            }
        }
    }

    override suspend fun deleteHabit(id: Int) {
        reminderManager?.cancelReminder(id)
        queries.transaction {
            queries.deleteLogsForHabit(id.toLong())
            queries.deleteHabit(id.toLong())
        }
    }

    override suspend fun toggleHabitCompletion(
        habitId: Int,
        date: LocalDate,
        value: Int,
    ) {
        queries.insertCompletion(habitId.toLong(), date.toString(), value.toLong())
    }

    private fun Habit_entity.toDomain(): Habit =
        Habit(
            id = id.toInt(),
            name = name,
            icon = icon,
            color = color,
            goalValue = goalValue.toInt(),
            goalUnit = goalUnit,
            frequency = frequency,
            repeatDays = repeatDays,
            reminderTime = reminderTime,
            startTime = startTime,
            endTime = endTime,
            habitType = HabitType.valueOf(habitType),
            createdAt = Instant.fromEpochMilliseconds(createdAt),
        )

    private fun Habit_log.toDomain(): HabitLog =
        HabitLog(
            id = id.toInt(),
            habitId = habitId.toInt(),
            actionTime = Instant.fromEpochMilliseconds(actionTime),
            targetDate = LocalDate.parse(targetDate),
            delta = delta.toInt(),
            newValue = newValue.toInt(),
        )
}
