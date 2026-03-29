package com.example.shared.di

import com.example.shared.db.DriverFactory
import com.example.shared.domain.reminder.ReminderManager
import com.example.shared.reminder.AndroidReminderManager
import org.koin.dsl.module

actual val platformModule = module {
    single { DriverFactory(get()) }
    single<ReminderManager> { AndroidReminderManager(get()) }
}
