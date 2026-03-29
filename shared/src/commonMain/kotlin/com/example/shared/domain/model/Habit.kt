package com.example.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Habit(
    val id: Int = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val goalValue: Int,
    val goalUnit: String,
    val frequency: String, // Hàng ngày, Tùy chỉnh, Ngày cụ thể
    val repeatDays: String? = null, // "1,2,3,4,5,6,7" HOẶC "2024-03-01,2024-03-15"
    val reminderTime: String?,
    val startTime: String? = null,
    val endTime: String? = null,
    val habitType: HabitType = HabitType.BUILD,
    val createdAt: Instant
)

enum class HabitType {
    BUILD, QUIT
}

data class HabitItem(
    val habit: Habit,
    val isCompleted: Boolean,
    val progress: Int,
    val date: LocalDate? = null
)
