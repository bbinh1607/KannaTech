package com.example.shared.domain.reminder

import com.example.shared.domain.model.Habit

interface ReminderManager {
    fun scheduleReminder(habit: Habit)
    fun cancelReminder(habitId: Int)
}
