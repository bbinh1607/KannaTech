package com.example.shared.domain.repository

import com.example.shared.domain.model.Habit
import com.example.shared.domain.model.HabitItem
import com.example.shared.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface HabitRepository {
    fun getAllHabitItems(date: LocalDate): Flow<List<HabitItem>>
    fun getHabitByIdFlow(id: Int): Flow<Habit?>
    suspend fun getHabitById(id: Int): Habit?
    suspend fun upsertHabit(habit: Habit)
    suspend fun deleteHabit(id: Int)
    suspend fun toggleHabitCompletion(habitId: Int, date: LocalDate, value: Int)
    fun getCompletionHistory(habitId: Int): Flow<List<HabitItem>>
    
    // Logging
    suspend fun insertLog(log: HabitLog)
    fun getLogsForHabit(habitId: Int): Flow<List<HabitLog>>
}
