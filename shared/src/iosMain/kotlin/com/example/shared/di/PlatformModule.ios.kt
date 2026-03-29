package com.example.shared.di

import com.example.shared.db.DriverFactory
import org.koin.dsl.module

actual val platformModule = module {
    single { DriverFactory() }
}
