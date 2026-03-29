package loli.kanna.di

import loli.kanna.auth.login.LoginViewModel
import loli.kanna.auth.register.RegisterViewModel
import loli.kanna.jobtracker.presentation.habit.create.CreateHabitViewModel
import loli.kanna.jobtracker.presentation.habit.detail.HabitDetailViewModel
import loli.kanna.jobtracker.presentation.habit.home.HabitHomeViewModel
import org.koin.dsl.module

val uiModule = module {
    factory { LoginViewModel(get(), get()) }
    factory { RegisterViewModel(get()) }
    factory { HabitHomeViewModel(get()) }
    factory { (habitId: Int?) -> CreateHabitViewModel(get(), habitId) }
    factory { (habitId: Int) -> HabitDetailViewModel(get(), habitId) }
}
