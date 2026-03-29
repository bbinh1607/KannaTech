package com.example.shared.data.dataSource.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.*

actual fun provideHttpClientEngine(): HttpClientEngine = OkHttp.create()
