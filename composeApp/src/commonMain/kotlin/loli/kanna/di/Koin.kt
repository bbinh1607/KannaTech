package loli.kanna.di

import com.example.shared.di.dataModule
import com.example.shared.di.platformModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            platformModule,
            dataModule,
            uiModule,
        )
    }
