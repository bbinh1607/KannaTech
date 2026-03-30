package loli.kanna.di

import loli.kanna.auth.login.LoginViewModel
import loli.kanna.auth.register.RegisterViewModel
import loli.kanna.jobtracker.presentation.habit.create.CreateHabitViewModel
import loli.kanna.jobtracker.presentation.habit.detail.HabitDetailViewModel
import loli.kanna.jobtracker.presentation.habit.home.HabitHomeViewModel
import loli.kanna.profile.ProfileViewModel
import loli.kanna.devicemanagement.ui.category.CategoryViewModel
import loli.kanna.devicemanagement.ui.device.DeviceListViewModel
import loli.kanna.devicemanagement.ui.device.DeviceDetailViewModel
import loli.kanna.devicemanagement.ui.device.ComponentDetailViewModel
import loli.kanna.devicemanagement.ui.component.ComponentViewModel
import org.koin.dsl.module

val uiModule = module {
    factory { LoginViewModel(get(), get()) }
    factory { RegisterViewModel(get()) }
    factory { HabitHomeViewModel(get()) }
    factory { (habitId: Int?) -> CreateHabitViewModel(get(), habitId) }
    factory { (habitId: Int) -> HabitDetailViewModel(get(), habitId) }
    factory { ProfileViewModel() }
    
    // Device Management
    factory { CategoryViewModel(get()) }
    factory { (categoryId: String) -> DeviceListViewModel(get(), categoryId) }
    factory { (deviceId: String) -> DeviceDetailViewModel(get(), deviceId) }
    factory { (id: String, isFromComponent: Boolean) -> 
        ComponentDetailViewModel(get(), id, isFromComponent) 
    }
    factory { ComponentViewModel(get()) }
}
