package com.example.shared.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class HabitLog(
    val id: Int = 0,
    val habitId: Int,
    val actionTime: Instant,
    val targetDate: LocalDate,
    val delta: Int,
    val newValue: Int
)
