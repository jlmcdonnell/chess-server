package dev.mcd.chess.di

import org.koin.core.context.startKoin

fun configureKoin() {
    startKoin {
        modules(appModule)
    }
}
